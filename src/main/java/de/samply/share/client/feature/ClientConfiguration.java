package de.samply.share.client.feature;

import static org.omnifaces.util.Faces.getServletContext;

import com.sun.jersey.spi.resource.Singleton;
import de.samply.config.util.FileFinderUtil;
import de.samply.share.common.utils.ProjectInfo;
import java.io.File;
import java.io.FileNotFoundException;
import javax.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.spi.FeatureManagerProvider;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

@Singleton
@ApplicationScoped
public class ClientConfiguration implements TogglzConfig, FeatureManagerProvider {

  private static final Logger logger = LogManager.getLogger(ClientConfiguration.class);
  private static final String featureProperties = "features.properties";

  private FeatureManager featureManager;

  @Override
  public FeatureManager getFeatureManager() {
    if (featureManager == null) {
      featureManager = new FeatureManagerBuilder()
          .featureEnum(ClientFeature.class)
          .stateRepository(getStateRepository())
          .userProvider(getUserProvider())
          .build();
    }

    return featureManager;
  }

  public Class<? extends Feature> getFeatureClass() {
    return ClientFeature.class;
  }

  /**
   * Create a StateRepository.
   *
   * @return StateRepository
   */
  public StateRepository getStateRepository() {
    File file;
    try {
      file = FileFinderUtil
          .findFile(featureProperties, ProjectInfo.INSTANCE.getProjectName().toLowerCase(),
              System.getProperty("catalina.base") + File.separator + "conf",
              getServletContext().getRealPath("/WEB-INF"));
    } catch (FileNotFoundException e) {
      logger.warn("Feature configuration not found.");
      return new EmptyStateRepository();
    }
    return new FileBasedStateRepository(file);
  }

  @Override
  public UserProvider getUserProvider() {
    return () -> new SimpleFeatureUser("<unused>", false);
  }

  @Override
  public int priority() {
    return 30;
  }
}
