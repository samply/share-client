package de.samply.share.client.quality.report.localdatamanagement;

import de.samply.share.model.common.Error;
import org.apache.http.HttpStatus;

public class LocalDataManagementResponse<T> {

  private Error error;
  private T response;
  private int statusCode;


  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  public T getResponse() {
    return response;
  }

  public void setResponse(T response) {
    this.response = response;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public boolean isSuccessful() {
    return error == null && statusCode == HttpStatus.SC_OK;
  }
}
