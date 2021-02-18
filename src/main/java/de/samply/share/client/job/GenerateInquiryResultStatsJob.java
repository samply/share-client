package de.samply.share.client.job;

import com.google.gson.Gson;
import de.samply.dktk.converter.PatientConverterUtil;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.GenerateInquiryResultStatsJobParams;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.InquiryResultStats;
import de.samply.share.client.model.graphdata.AgeDistribution;
import de.samply.share.client.model.graphdata.GenderDistribution;
import de.samply.share.client.quality.report.logger.PercentageLogger;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.client.util.db.InquiryResultStatsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.utils.Converter;
import javax.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

/**
 * This job reads the result set of a given inquiry and extracts basic statistical information from
 * it. Currently, only age and gender distribution are counted and written to the database. This
 * enables displaying the graphs on the show_inquiry page without additional delay that would be
 * caused by having to read all result files again.
 */
public class GenerateInquiryResultStatsJob implements Job {

  private static final Logger logger = LogManager.getLogger(GenerateInquiryResultStatsJob.class);

  private final LdmConnector ldmConnector;
  private InquiryResult inquiryResult;

  public GenerateInquiryResultStatsJob() {
    this.ldmConnector = ApplicationBean.getLdmConnector();
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {

    JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
    JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
    GenerateInquiryResultStatsJobParams jobParams = new GenerateInquiryResultStatsJobParams(
        dataMap);

    inquiryResult = InquiryResultUtil.fetchInquiryResultById(jobParams.getInquiryResultId());

    logger.debug(jobKey.toString() + " " + jobParams);

    //Don't do anything if the stats are already available. Maybe add an override/force option later
    if (InquiryResultStatsUtil
        .getInquiryResultStatsForInquiryResultById(jobParams.getInquiryResultId()) == null) {
      logger.debug("Stats not done...calculating");
      try {
        Object queryResult = ldmConnector.getResults(inquiryResult.getLocation());
        generateStatistics(queryResult);
      } catch (LdmConnectorException e) {
        logger.error("Error connecting to local datamanagement");
      }
    } else {
      logger.debug("Stats already done");
    }
  }

  /**
   * Take a full query result as retrieved from local datamanagement, extract the stats and write
   * them to the database.
   *
   * @param queryResult query result from local datamanagement
   */
  private void generateStatistics(Object queryResult) {
    AgeDistribution ageDistribution = new AgeDistribution();
    GenderDistribution genderDistribution = new GenderDistribution();

    switch (ApplicationUtils.getConnectorType()) {
      case DKTK:
        QueryResult ccpQueryResult = (QueryResult) queryResult;
        PercentageLogger percentageLogger =
            new PercentageLogger(logger, ccpQueryResult.getPatient().size(),
                "generating statistics...");

        for (Patient patient : ccpQueryResult.getPatient()) {
          de.samply.share.model.common.Patient patientCommon =
              new de.samply.share.model.common.Patient();
          try {
            patientCommon = Converter.convertCcpPatientToCommonPatient(patient);
          } catch (JAXBException e) {
            e.printStackTrace();
          }
          ageDistribution.incrementCountForAge(getAge(patientCommon,
              "urn:dktk:dataelement:28:"));
          genderDistribution.increaseCountForGender(getGender(patientCommon,
              "urn:dktk:dataelement:1:"));

          percentageLogger.incrementCounter();
        }
        break;

      case SAMPLY:
        for (de.samply.share.model.osse.Patient donor : ((BbmriResult) queryResult).getDonors()) {
          de.samply.share.model.common.Patient donorCommon =
              new de.samply.share.model.common.Patient();
          try {
            donorCommon = Converter.convertOssePatientToCommonPatient(donor);
          } catch (JAXBException e) {
            e.printStackTrace();
          }
          ageDistribution.incrementCountForAge(getAge(donorCommon,
              "urn:mdr16:dataelement:22:"));
          genderDistribution
              .increaseCountForGender(getGender(donorCommon,
                  "urn:mdr16:dataelement:23:"));
        }
        break;
      default:
        break;
    }

    Gson gson = new Gson();

    InquiryResultStats inquiryResultStats = new InquiryResultStats();
    inquiryResultStats.setStatsAge(gson.toJson(ageDistribution));
    inquiryResultStats.setStatsGender(gson.toJson(genderDistribution));
    inquiryResultStats.setInquiryResultId(inquiryResult.getId());
    InquiryResultStatsUtil.insertInquiryResultStats(inquiryResultStats);
  }

  /**
   * Get the age of a given patient.
   *
   * @param patient the patient to check
   * @return the age of the patient
   */
  private int getAge(de.samply.share.model.common.Patient patient, String mdrAge) {
    MdrIdDatatype mdrIdDatatypeAge = new MdrIdDatatype(mdrAge);
    String ageString = PatientConverterUtil.getFirstValueForKey(patient, mdrIdDatatypeAge);
    if (ageString == null) {
      return -1;
    } else {
      try {
        if (ApplicationUtils.isSamply()) {
          ageString = ageString.replace("\"", "");
          LocalDate birthdate = new LocalDate(Integer.parseInt(ageString.split("\\.")[2]),
              Integer.parseInt(ageString.split("\\.")[1]),
              Integer.parseInt(ageString.split("\\.")[0]));
          LocalDate now = new LocalDate();
          Years age = Years.yearsBetween(birthdate, now);
          return age.getYears();
        }
        return Integer.parseInt(ageString);
      } catch (NumberFormatException e) {
        return -1;
      }
    }
  }

  /**
   * Get the gender of a given patient.
   *
   * @param patient the patient to check
   * @return the gender of the patient
   */
  private String getGender(de.samply.share.model.common.Patient patient, String mdrGender) {
    MdrIdDatatype mdrIdDatatypeGender = new MdrIdDatatype(mdrGender);
    String gender = PatientConverterUtil.getFirstValueForKey(patient, mdrIdDatatypeGender);
    if (gender == null) {
      return "";
    } else {
      return gender;
    }
  }
}
