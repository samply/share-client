/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.job;

import com.google.gson.Gson;
import de.samply.common.ldmclient.samplystoreBiobank.LdmClientSamplystoreBiobank;
import de.samply.dktk.converter.PatientConverterUtil;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.job.params.GenerateInquiryResultStatsJobParams;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.model.db.tables.pojos.InquiryResultStats;
import de.samply.share.client.model.graphData.AgeDistribution;
import de.samply.share.client.model.graphData.GenderDistribution;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.LdmConnectorSamplystoreBiobank;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.InquiryResultStatsUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.bbmri.Donor;
import de.samply.share.model.ccp.Entity;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.utils.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.quartz.*;

import javax.xml.bind.JAXBException;

/**
 * This job reads the result set of a given inquiry and extracts basic statistical information from it
 * <p>
 * Currently, only age and gender distribution are counted and written to the database. This enables displaying the
 * graphs on the show_inquiry page without additional delay that would be caused by having to read all result files again
 */
public class GenerateInquiryResultStatsJob implements Job {

    private static final Logger logger = LogManager.getLogger(GenerateInquiryResultStatsJob.class);

    private GenerateInquiryResultStatsJobParams jobParams;
    private JobKey jobKey;
    private LdmConnector ldmConnector;
    private InquiryResult inquiryResult;
    private Object queryResult;
    private MdrIdDatatype MDR_KEY_GENDER;
    private MdrIdDatatype MDR_KEY_AGE;

    public GenerateInquiryResultStatsJob() {
        this.ldmConnector = ApplicationBean.getLdmConnector();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        jobKey = jobExecutionContext.getJobDetail().getKey();
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        jobParams = new GenerateInquiryResultStatsJobParams(dataMap);

        inquiryResult = InquiryResultUtil.fetchInquiryResultById(jobParams.getInquiryResultId());

        logger.debug(jobKey.toString() + " " + jobParams);

        MDR_KEY_AGE = new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_AGE_AT_DIAGNOSIS));
        MDR_KEY_GENDER = new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_GENDER));

        // Don't do anything if the stats are already available. Maybe add an override/force option later
        if (InquiryResultStatsUtil.getInquiryResultStatsForInquiryResultById(jobParams.getInquiryResultId()) == null) {
            logger.debug("Stats not done...calculating");
            try {
                queryResult = ldmConnector.getResults(inquiryResult.getLocation());
                generateStatistics(queryResult);
            } catch (LDMConnectorException e) {
                logger.error("Error connecting to local datamanagement");
            }
        } else {
            logger.debug("Stats already done");
        }
    }

    /**
     * Take a full query result as retrieved from local datamanagement, extract the stats and write them to the database
     *
     * @param queryResult query result from local datamanagement
     */
    private void generateStatistics(Object queryResult) {
        AgeDistribution ageDistribution = new AgeDistribution();
        GenderDistribution genderDistribution = new GenderDistribution();

        // TODO: other types
        if (ldmConnector instanceof LdmConnectorCentraxx) {
            QueryResult ccpQueryResult = (QueryResult) queryResult;
            for (Patient patient : ccpQueryResult.getPatient()) {
                de.samply.share.model.common.Patient patientCommon = new de.samply.share.model.common.Patient();
                try {
                    patientCommon = Converter.convertCCPPatientToCommonPatient(patient);
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                ageDistribution.incrementCountForAge(getAge(patientCommon,"urn:dktk:dataelement:28:"));
                genderDistribution.increaseCountForGender(getGender(patientCommon,"urn:dktk:dataelement:1:"));
            }
        }
        if (ldmConnector instanceof LdmConnectorSamplystoreBiobank) {
            for (de.samply.share.model.osse.Patient donor : ((BbmriResult) queryResult).getDonors()) {
                de.samply.share.model.common.Patient donorCommon = new de.samply.share.model.common.Patient();
                try {
                    donorCommon = Converter.convertOssePatientToCommonPatient(donor);
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                ageDistribution.incrementCountForAge(getAge(donorCommon,"urn:mdr16:dataelement:22:"));
                genderDistribution.increaseCountForGender(getGender(donorCommon,"urn:mdr16:dataelement:23:"));
            }

        }

        Gson gson = new Gson();

        InquiryResultStats inquiryResultStats = new InquiryResultStats();
        inquiryResultStats.setStatsAge(gson.toJson(ageDistribution));
        inquiryResultStats.setStatsGender(gson.toJson(genderDistribution));
        inquiryResultStats.setInquiryResultId(inquiryResult.getId());
        InquiryResultStatsUtil.insertInquiryResultStats(inquiryResultStats);
    }

    /**
     * Get the age of a given patient
     *
     * @param patient the patient to check
     * @return the age of the patient
     */
    private int getAge(de.samply.share.model.common.Patient patient, String mdr_age) {
        MdrIdDatatype mdrIdDatatypeAge = new MdrIdDatatype(mdr_age);
        String ageString = de.samply.share.client.util.PatientConverterUtil.getFirstValueForKey(patient, mdrIdDatatypeAge);
        if (ageString == null) {
            return -1;
        } else {
            try {
                if(ldmConnector instanceof LdmConnectorSamplystoreBiobank){
                    LocalDate birthdate = new LocalDate (Integer.parseInt(ageString.split("\\.")[2]), Integer.parseInt(ageString.split("\\.")[1]), Integer.parseInt(ageString.split("\\.")[0]));
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
     * Get the gender of a given patient
     *
     * @param patient the patient to check
     * @return the gender of the patient
     */
    private String getGender(de.samply.share.model.common.Patient patient, String mdr_gender) {
        MdrIdDatatype mdrIdDatatypeGender = new MdrIdDatatype(mdr_gender);
        String gender = de.samply.share.client.util.PatientConverterUtil.getFirstValueForKey(patient, mdrIdDatatypeGender);
        if (gender == null) {
            return "";
        } else {
            return gender;
        }
    }
}
