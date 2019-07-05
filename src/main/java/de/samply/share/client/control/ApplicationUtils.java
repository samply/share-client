package de.samply.share.client.control;

import de.samply.share.client.model.db.enums.QueryLanguageType;
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

    public static boolean isLanguageQuery() {
        return getQueryLanguageType() == QueryLanguageType.QUERY;
    }

    public static boolean isLanguageCql() {
        return getQueryLanguageType() == QueryLanguageType.CQL;
    }

    private static QueryLanguageType getQueryLanguageType() {
        return QueryLanguageType.valueOf(ApplicationBean.getBridgeheadInfos().getQueryLanguage());
    }
}
