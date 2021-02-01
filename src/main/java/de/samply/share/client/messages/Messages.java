package de.samply.share.client.messages;

import de.samply.share.client.control.ApplicationBean;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provide access to resource bundles in order to get localized messages.
 */
public class Messages {

  private static final String BUNDLE_NAME = "de.samply.share.client.messages.messages";

  private Messages() {
  }

  /**
   * Gets the localized expression.
   *
   * @param key the expression to translate
   * @return the translated expression
   */
  public static String getString(String key) {
    try {
      return getResourceBundle().getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  /**
   * Gets the localized expression with potential parameters. E.g. when the message bundle entry is
   * "greeting=Hello {0}, this is {1}".
   *
   * @param key    the expression to translate
   * @param params the parameters to insert to the message
   * @return the translated message
   */
  public static String getString(String key, Object... params) {
    try {
      return MessageFormat.format(getResourceBundle().getString(key), params);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  private static ResourceBundle getResourceBundle() {
    return ResourceBundle.getBundle(BUNDLE_NAME, ApplicationBean.getLocale());
  }
}
