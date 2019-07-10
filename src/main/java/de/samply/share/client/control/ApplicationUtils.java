package de.samply.share.client.control;

import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.common.utils.ProjectInfo;

public class ApplicationUtils {

    private static final String PREFIX_QUERY_LANGUAGE_TYPE = "QL_";

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
        return getQueryLanguageType() == QueryLanguageType.QL_QUERY;
    }

    public static boolean isLanguageCql() {
        return getQueryLanguageType() == QueryLanguageType.QL_CQL;
    }

    private static QueryLanguageType getQueryLanguageType() {
        String queryLanguage = ApplicationBean.getBridgeheadInfos().getQueryLanguage();

        return QueryLanguageType.valueOf(PREFIX_QUERY_LANGUAGE_TYPE + queryLanguage);
    }
}
