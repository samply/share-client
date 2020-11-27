package de.samply.share.client.model.centralsearch;

/**
 * The outcome of the upload of a patient dataset.
 */
public class PatientUploadResult {

  private int status;
  private String message;
  private boolean retry;
  private boolean success;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isRetry() {
    return retry;
  }

  public void setRetry(boolean retry) {
    this.retry = retry;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  @Override
  public String toString() {
    return "PatientUploadResult{"
        + "status=" + status
        + ", message='" + message + '\''
        + ", retry=" + retry
        + ", success=" + success
        + '}';
  }
}
