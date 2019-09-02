package de.samply.share.client.util.connector;

import java.util.List;

public class LdmPostQueryParameterView extends AbstractLdmPostQueryParameter {

    private final List<String> removeKeysFromView;
    private final boolean completeMdsViewFields;
    private final boolean includeAdditionalViewfields;

    /**
     * Parameter for posting a query to local datamanagement.
     *
     * @param statisticsOnly              if true, set a parameter to only request a count of the results, not the whole result lists
     * @param removeKeysFromView          A list of keys to be removed from the query (and viewfields)
     * @param completeMdsViewFields       if true, add all entries from mds-b and mds-k to viewfields
     * @param includeAdditionalViewfields if true, check if there are additional viewfields to set in the database. For uploads to central
     *                                    mds database, this should be false
     */
    public LdmPostQueryParameterView(boolean statisticsOnly, List<String> removeKeysFromView, boolean completeMdsViewFields, boolean includeAdditionalViewfields) {
        super(statisticsOnly);

        this.removeKeysFromView = removeKeysFromView;
        this.completeMdsViewFields = completeMdsViewFields;
        this.includeAdditionalViewfields = includeAdditionalViewfields;
    }

    List<String> getRemoveKeysFromView() {
        return removeKeysFromView;
    }

    boolean isCompleteMdsViewFields() {
        return completeMdsViewFields;
    }

    boolean isIncludeAdditionalViewfields() {
        return includeAdditionalViewfields;
    }
}
