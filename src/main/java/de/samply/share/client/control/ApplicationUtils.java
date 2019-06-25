package de.samply.share.client.control;

import de.samply.share.common.utils.ProjectInfo;

public class ApplicationUtils {
    public static boolean isDktk() {
        return ProjectInfo.INSTANCE.getProjectName().equalsIgnoreCase("dktk");
    }

    public static boolean isSamply() {
        return ProjectInfo.INSTANCE.getProjectName().equalsIgnoreCase("samply");
    }
}
