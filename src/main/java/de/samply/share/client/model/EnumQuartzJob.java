package de.samply.share.client.model;

public enum EnumQuartzJob {
    DIRECTORY_GROUP("DirectoryGroup"),
    CENTRAL_SEARCH_GROUP("CentralSearchGroup");

    private final String text;

    /**
     * @param text
     */
    EnumQuartzJob(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
