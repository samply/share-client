package de.samply.share.client.feature;

import de.samply.config.util.FileFinderUtil;
import de.samply.share.common.utils.ProjectInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;

import static org.omnifaces.util.Faces.getServletContext;

@ApplicationScoped
public class ClientConfiguration implements TogglzConfig {
    private static final Logger logger = LogManager.getLogger(ClientConfiguration.class);
    private String FEATURE_PROPERTIES = "features.properties";

    public Class<? extends Feature> getFeatureClass() {
        return ClientFeature.class;
    }

    public StateRepository getStateRepository() {
        File file;
        try {
            file = FileFinderUtil.findFile(FEATURE_PROPERTIES, ProjectInfo.INSTANCE.getProjectName().toLowerCase(), System.getProperty("catalina.base") + File.separator + "conf", getServletContext().getRealPath("/WEB-INF"));
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
}