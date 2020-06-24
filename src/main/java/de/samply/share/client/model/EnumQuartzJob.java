package de.samply.share.client.model;

public enum EnumQuartzJob {
    DIRECTORY_GROUP("DirectoryGroup"),
    CENTRAL_SEARCH_GROUP("CentralSearchGroup");

    private final String name;

    /**
     * @param name
     */
    EnumQuartzJob(final String name) {
        this.name = name;
    }


    public String getName(){
        return name;
    }
}
