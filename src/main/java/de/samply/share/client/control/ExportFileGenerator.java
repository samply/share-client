package de.samply.share.client.control;

import de.samply.dktk.converter.EnumValidationHandling;
import de.samply.dktk.converter.PatientConverter;
import de.samply.dktk.converter.PatientConverterUtil;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.tables.pojos.Contact;
import de.samply.share.client.model.db.tables.pojos.Inquiry;
import de.samply.share.client.model.db.tables.pojos.InquiryResult;
import de.samply.share.client.util.WebUtils;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.ccp.QueryResult;
import de.samply.web.mdrfaces.MdrContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates export file.
 */
public class ExportFileGenerator {

  private static final Logger logger = LoggerFactory.getLogger(ExportFileGenerator.class);

  private static int DEFAULT_TIMEOUT_IN_MINUTEN = 6 * 60;
  private long timeoutInNanoseconds;

  private InquiryResult latestInquiryResult;
  private LdmConnector ldmConnector;
  private Inquiry inquiry;
  private Contact selectedInquiryContact;
  private EnumValidationHandling validationHandling;

  private ByteArrayOutputStream byteArrayOutputStream;


  /**
   * Constructor for generating export file.
   *
   * @param latestInquiryResult latest inquiry result
   * @param ldmConnector local data management connector
   * @param inquiry inquiry
   * @param selectedInquiryContact selected inquiry contact
   * @param validationHandling validation handling
   * @param timeoutInMinutes timeout in minutes
   *
   */
  public ExportFileGenerator(
      InquiryResult latestInquiryResult,
      LdmConnector ldmConnector, Inquiry inquiry,
      Contact selectedInquiryContact,
      EnumValidationHandling validationHandling,
      Integer timeoutInMinutes) {

    this.latestInquiryResult = latestInquiryResult;
    this.ldmConnector = ldmConnector;
    this.inquiry = inquiry;
    this.selectedInquiryContact = selectedInquiryContact;
    this.validationHandling = validationHandling;
    timeoutInMinutes = (timeoutInMinutes != null) ? timeoutInMinutes : DEFAULT_TIMEOUT_IN_MINUTEN;
    this.timeoutInNanoseconds = timeoutInMinutes * 6 * 10000000000L;

  }

  /**
   * Generate export file.
   *
   * @return export file in byte array output stream
   * @throws ExportFileGeneratorException export file generator exception.
   */
  public ByteArrayOutputStream generateExport() throws ExportFileGeneratorException {

    Runnable runnable = () -> generateExportByteStreamAndNotifyAll();

    Thread thread = new Thread(runnable);
    thread.start();

    return getByteArrayOutputStream();

  }


  private synchronized void generateExportByteStreamAndNotifyAll() {
    byteArrayOutputStream = generateExportByteStream();
    notifyAll();
  }

  private synchronized ByteArrayOutputStream getByteArrayOutputStream()
      throws ExportFileGeneratorException {
    try {

      wait(timeoutInNanoseconds);
      return byteArrayOutputStream;

    } catch (InterruptedException e) {
      throw new ExportFileGeneratorException(e);
    }

  }


  /**
   * Generate an Excel Workbook for the inquiry result and send it to the client.
   *
   */
  private ByteArrayOutputStream generateExportByteStream() {
    logger.debug("Generate Export File");

    // Add a list of mdr items that will not be included in the export.
    List<MdrIdDatatype> blacklist = getExportMdrBlackList();

    try {
      String queryResultLocation = latestInquiryResult.getLocation();
      Integer exportWorbookWindow = ConfigurationUtil
          .getConfigurationElementValueAsInteger(EnumConfiguration.EXPORT_WORKBOOK_WINDOW);

      PatientConverter patientConverter = new PatientConverter(
          MdrContext.getMdrContext().getMdrClient(),
          ApplicationBean.getMdrValidator(),
          validationHandling,
          blacklist,
          exportWorbookWindow);
      // TODO Use switch statement on ApplicationUtils.getConnectorType()
      Workbook workbook = null;
      if (ApplicationUtils.isDktk()) {
        QueryResult queryResult = (QueryResult) ldmConnector.getResults(queryResultLocation);
        logger.debug("Result completely loaded...write excel file");
        String executionDateString = WebUtils.getExecutionDate(latestInquiryResult);
        workbook = patientConverter.centraxxQueryResultToExcel(queryResult,
            PatientConverterUtil
                .createInquiryObjectForInfoSheet(inquiry.getLabel(), inquiry.getDescription()),
            PatientConverterUtil.createContactObjectForInfoSheet(selectedInquiryContact.getTitle(),
                selectedInquiryContact.getFirstName(), selectedInquiryContact.getLastName()),
            ConfigurationUtil
                .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID),
            executionDateString
        );
      } else if (ApplicationUtils.isSamply()) {
        BbmriResult queryResult = (BbmriResult) ldmConnector.getResults(queryResultLocation);
        logger.debug("Result completely loaded...write excel file");
        String executionDateString = WebUtils.getExecutionDate(latestInquiryResult);
        workbook = patientConverter.biobanksQueryResultToExcel(queryResult,
            PatientConverterUtil
                .createInquiryObjectForInfoSheet(inquiry.getLabel(), inquiry.getDescription()),
            PatientConverterUtil.createContactObjectForInfoSheet(selectedInquiryContact.getTitle(),
                selectedInquiryContact.getFirstName(), selectedInquiryContact.getLastName()),
            ConfigurationUtil
                .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID),
            executionDateString
        );
      }

      logger.debug("Workbook complete");

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try {
        workbook.write(bos);
      } finally {
        bos.close();
      }

      String lastExportFilename = generateLastExportFilename();
      createTemporaryFile(bos, lastExportFilename);

      return bos;

    } catch (Exception e) {

      logger.error("Exception caught while trying to export data", e);
      return null;

    }
  }


  private List<MdrIdDatatype> getExportMdrBlackList() {

    try {
      List<MdrIdDatatype> mdrIdDatatypeList = new ArrayList<>();

      List<String> configurationElementValueList = ConfigurationUtil
          .getConfigurationElementValueList(EnumConfiguration.EXPORT_MDR_BLACKLIST);

      for (String mdrIds : configurationElementValueList) {
        if (mdrIds.length() > 0) {
          mdrIdDatatypeList.add(new MdrIdDatatype(mdrIds));
        }
      }

      return mdrIdDatatypeList;

    } catch (Exception e) {
      logger.error(e.getMessage(),e);
      return new ArrayList<>();
    }

  }

  private String generateLastExportFilename() {
    String timestampForFilename = getTimestampForFilename(new Date());

    return "export-" + timestampForFilename + ".xlsx";
  }

  private String getTimestampForFilename(Date timestamp) {

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.ENGLISH);
    return simpleDateFormat.format(timestamp);

  }

  private void createTemporaryFile(ByteArrayOutputStream byteArrayOutputStream, String filename) {

    try {

      String path = ConfigurationUtil
          .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
      filename = path + File.separator + filename;
      FileOutputStream fileOutputStream = new FileOutputStream(filename);
      byteArrayOutputStream.writeTo(fileOutputStream);

    } catch (IOException e) {
      logger.error(e.getMessage(),e);
    }
  }

}
