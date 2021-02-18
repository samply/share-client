package de.samply.share.client.model.line;

import java.io.Serializable;

/**
 * Log element to show on the Log Viewer page.
 */
public class InquiryLine implements Serializable {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The inquiry id.
   */
  private int id;

  /**
   * The inquiry name.
   */
  private String name;

  /**
   * What entities are searched for.
   */
  private String searchFor;

  /**
   * When was the inquiry received from the broker.
   */
  private String receivedAt;

  /**
   * When was the inquiry archived.
   */
  private String archivedAt;

  /**
   * How many results were found.
   */
  private String found;

  /**
   * When were the results found.
   */
  private String asOf;

  /**
   * What is the name of the broker that delivered this inquiry.
   */
  private String brokerName;

  /**
   * If there was an error code...show it.
   */
  private String errorCode;

  /**
   * Has the user already seen this?.
   */
  private boolean seen;

  /**
   * Get the Id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Set the Id.
   *
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Get the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the search for.
   *
   * @return the searchFor
   */
  public String getSearchFor() {
    return searchFor;
  }

  /**
   * Set the search for.
   *
   * @param searchFor the searchFor to set
   */
  public void setSearchFor(String searchFor) {
    this.searchFor = searchFor;
  }

  /**
   * Get the received at.
   *
   * @return the receivedAt
   */
  public String getReceivedAt() {
    return receivedAt;
  }

  /**
   * Set the received at.
   *
   * @param receivedAt the receivedAt to set
   */
  public void setReceivedAt(String receivedAt) {
    this.receivedAt = receivedAt;
  }

  public String getArchivedAt() {
    return archivedAt;
  }

  public void setArchivedAt(String archivedAt) {
    this.archivedAt = archivedAt;
  }

  /**
   * Get the found.
   *
   * @return the found
   */
  public String getFound() {
    return found;
  }

  /**
   * Set the found.
   *
   * @param found the found to set
   */
  public void setFound(String found) {
    this.found = found;
  }

  /**
   * Get the asOf.
   *
   * @return the asOf
   */
  public String getAsOf() {
    return asOf;
  }

  /**
   * Set the asOf.
   *
   * @param asOf the asOf to set
   */
  public void setAsOf(String asOf) {
    this.asOf = asOf;
  }

  /**
   * Get the broker name.
   *
   * @return the brokerName
   */
  public String getBrokerName() {
    return brokerName;
  }

  /**
   * Set the broker name.
   *
   * @param brokerName the brokerName to set
   */
  public void setBrokerName(String brokerName) {
    this.brokerName = brokerName;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public boolean isSeen() {
    return seen;
  }

  public void setSeen(boolean seen) {
    this.seen = seen;
  }
}
