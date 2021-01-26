package de.samply.share.client.model.check;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the result of a connection or function check.
 */
public class CheckResult {

  private boolean success;
  private Date executionDate;
  private List<Message> messages;
  private String panelClass;

  public CheckResult() {
    messages = new ArrayList<>();
    panelClass = "panel-default";
  }

  public boolean isSuccess() {
    return success;
  }

  /**
   * Set the value of success.
   *
   * @param success if the result of the check was a success or not
   */
  public void setSuccess(boolean success) {
    this.success = success;
    if (success == true) {
      setPanelClass("panel-success");
    } else {
      setPanelClass("panel-danger");
    }
  }

  public Date getExecutionDate() {
    return executionDate;
  }

  public void setExecutionDate(Date executionDate) {
    this.executionDate = executionDate;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public String getPanelClass() {
    return panelClass;
  }

  public void setPanelClass(String panelClass) {
    this.panelClass = panelClass;
  }
}
