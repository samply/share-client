package de.samply.share.client.model.check;

/**
 * A message, consisting of text and an icon class, as used in the check result.
 */
public class Message {

  private String entry;
  private String iconClass;

  public Message(String entry) {
    this.entry = entry;
  }

  public Message(String entry, String iconClass) {
    this.entry = entry;
    this.iconClass = iconClass;
  }

  public String getEntry() {
    return entry;
  }

  public void setEntry(String entry) {
    this.entry = entry;
  }

  public String getIconClass() {
    return iconClass;
  }

  public void setIconClass(String iconClass) {
    this.iconClass = iconClass;
  }

  @Override
  public String toString() {
    return "Message{"
        + "entry='" + entry + '\''
        + ", iconClass='" + iconClass + '\''
        + '}';
  }
}
