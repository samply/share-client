package de.samply.share.client.model;

/**
 * Represents the information, centraxx provides when the info resource is read
 */
public class CentraxxInfo {

    private String centraxxVersion;
    private String currentSessionCount;
    private String status;

    /**
     * @return the centraxxVersion
     */
    public String getCentraxxVersion() {
        return centraxxVersion;
    }
    /**
     * @param centraxxVersion the centraxxVersion to set
     */
    public void setCentraxxVersion(String centraxxVersion) {
        this.centraxxVersion = centraxxVersion;
    }
    /**
     * @return the currentSessionCount
     */
    public String getCurrentSessionCount() {
        return currentSessionCount;
    }
    /**
     * @param currentSessionCount the currentSessionCount to set
     */
    public void setCurrentSessionCount(String currentSessionCount) {
        this.currentSessionCount = currentSessionCount;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}