package de.samply.share.client.control;

import de.samply.share.common.utils.ProjectInfo;

public class ApplicationUtils {

    public static boolean isDktk() {
        return getConnectorType() == ConnectorType.DKTK;
    }

    public static boolean isSamply() {
        return getConnectorType() == ConnectorType.SAMPLY;
    }

    public static ConnectorType getConnectorType() {
        return ConnectorType.from(ProjectInfo.INSTANCE.getProjectName());
    }
}
