package de.samply.share.client.quality.report.model.mdr;

import de.dth.mdr.validator.MdrConnection;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.properties.PropertyUtils;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.util.Arrays;
import java.util.List;

public class MdrConnectionFactory {

  private static String authUserId;
  private static String authKeyId;
  private static String authUrl;
  private static String privateKeyBase64;
  private static List<String> namespaces;

  /**
   * Todo.
   */
  public MdrConnectionFactory() {

    authUserId = loadProperty(Properties.AUTH_USER_ID);
    authKeyId = loadProperty(Properties.AUTH_KEY_ID);
    authUrl = loadProperty(Properties.AUTH_URL);
    privateKeyBase64 = loadProperty(Properties.AUTH_PRIVATE_KEY_BASE_64);
    namespaces = getNamespaces();

  }

  private List<String> getNamespaces() {
    return Arrays
        .asList(PropertyUtils.getListOfProperties(Properties.NAMESPACE.getEnumConfiguration()));
  }

  private String loadProperty(Properties property) {
    return ConfigurationUtil.getConfigurationElementValue(property.getEnumConfiguration());
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public MdrConnection getMdrConnection() {

    String mdrUrl = getMdrUrl();
    HttpConnector httpConnector = ApplicationBean.createHttpConnector();

    return getMdrConnection(mdrUrl, authUserId, authKeyId, authUrl, privateKeyBase64, namespaces,
        httpConnector);

  }

  private MdrConnection getMdrConnection(String mdrUrl, String authUserId, String keyId,
      String authUrl, String privateKeyBase64, List<String> namespaces,
      HttpConnector httpConnector43) {
    return new MdrConnection(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, namespaces,
        true, httpConnector43);
  }

  private String getMdrUrl() {
    return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL);
  }


  private enum Properties {

    NAMESPACE(EnumConfiguration.QUALITY_REPORT_MDR_NAMESPACE),
    AUTH_USER_ID(EnumConfiguration.QUALITY_REPORT_MDR_AUTH_USER_ID),
    AUTH_KEY_ID(EnumConfiguration.QUALITY_REPORT_MDR_AUTH_KEY_ID),
    AUTH_URL(EnumConfiguration.QUALITY_REPORT_MDR_AUTH_URL),
    AUTH_PRIVATE_KEY_BASE_64(EnumConfiguration.QUALITY_REPORT_MDR_AUTH_PRIVATE_KEY_BASE_64);

    private final EnumConfiguration enumConfiguration;

    Properties(EnumConfiguration enumConfiguration) {
      this.enumConfiguration = enumConfiguration;
    }

    public EnumConfiguration getEnumConfiguration() {
      return enumConfiguration;
    }

  }


}
