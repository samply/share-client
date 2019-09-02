package de.samply.share.client.util.connector;

public abstract class AbstractLdmPostQueryParameter {

    private final boolean statisticsOnly;

    /**
     * Parameter for posting a query to local datamanagement.
     *
     * @param statisticsOnly              if true, set a parameter to only request a count of the results, not the whole result lists
     */
    AbstractLdmPostQueryParameter(boolean statisticsOnly) {
        this.statisticsOnly = statisticsOnly;
    }

    public boolean isStatisticsOnly() {
        return statisticsOnly;
    }
}
