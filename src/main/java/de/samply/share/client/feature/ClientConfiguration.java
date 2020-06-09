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
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
            logger.debug("Feature configuration not found. Creating an empty one");
            file = createFile();
        }
        return new FileBasedStateRepository(file);
    }

    private File createFile() {
        ServletContext ctx = getServletContext();
        String path = ctx.getRealPath("WEB-INF/conf/");
        File file = new File(path + "feature.properties");
        try {
            if (file.createNewFile())
                return file;
        } catch (IOException e) {
            logger.error("Could not create a new feature.properties file");
        }
        return null;
    }

    @Override
    public UserProvider getUserProvider() {
        return () -> new SimpleFeatureUser("<unused>", false);
    }
}
