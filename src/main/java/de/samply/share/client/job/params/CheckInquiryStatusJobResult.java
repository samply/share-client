package de.samply.share.client.job.params;

/**
 * Used as result to be interpreted by the CheckInquiryStatusJobListener.
 */
public class CheckInquiryStatusJobResult {

  private final boolean isRescheduled;
  private final boolean resetStatusFlags;

  public CheckInquiryStatusJobResult(boolean isRescheduled, boolean resetStatusFlags) {
    this.isRescheduled = isRescheduled;
    this.resetStatusFlags = resetStatusFlags;
  }

  public boolean isRescheduled() {
    return isRescheduled;
  }

  public boolean isResetStatusFlags() {
    return resetStatusFlags;
  }
}
