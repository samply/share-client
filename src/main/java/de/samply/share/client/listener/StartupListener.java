package de.samply.share.client.listener;

import de.samply.config.util.FileFinderUtil;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.common.utils.ProjectInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

/**
 * An implementation of a ServletContextListener. On startup, sets message resolvers, checks if the
 * database has to be upgraded. Before shutdown, deregister drivers.
 */
@WebListener
public class StartupListener implements ServletContextListener {

  private static final Logger logger = LogManager.getLogger(StartupListener.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    logger.info("context initialized!");
    setMessagesResolver();
    ProjectInfo.INSTANCE.initProjectMetadata(sce);
    try {
      Configurator.initialize(
          null,
          FileFinderUtil.findFile(
              "log4j2.xml", ProjectInfo.INSTANCE.getProjectName(),
              System.getProperty("catalina.base") + File.separator + "conf",
              sce.getServletContext().getRealPath("/WEB-INF")).getAbsolutePath());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    EventLogUtil.insertEventLogEntry(EventMessageType.E_SYSTEM_SHUTDOWN);
    // This tries to manually deregister the JDBC driver, which prevents Tomcat 7 from complaining
    // about memory leaks to this class
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        logger.info("Deregistered driver " + driver);
      } catch (SQLException e) {
        logger.fatal("Error deregistering driver:" + driver + "\n" + e.getMessage());
      }
    }
  }

  /**
   * Set Omnifaces Messages Resolver (see http://showcase.omnifaces.org/utils/Messages ).
   */
  private void setMessagesResolver() {
    logger.debug("Setting message resolver");
    Messages.setResolver(new Messages.Resolver() {
      private static final String BASE_NAME = "de.samply.share.client.messages.messages";

      public String getMessage(String message, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, Faces.getLocale());
        if (bundle.containsKey(message)) {
          message = bundle.getString(message);
        }
        return params.length > 0 ? MessageFormat.format(message, params) : message;
      }
    });
  }
}
