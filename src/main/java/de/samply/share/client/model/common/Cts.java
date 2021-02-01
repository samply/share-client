package de.samply.share.client.model.common;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Java class for anonymous complex type. The following schema fragment specifies the expected
 * content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="mainzellisteUrl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="mainzellisteApiKey" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "username",
    "password",
    "url",
    "profile",
    "mainzellisteUrl",
    "mainzellisteApiKey",
    "searchIdType"
})
@XmlRootElement(name = "cts", namespace = "http://schema.samply.de/config/CtsInfo")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
    comments = "JAXB RI v2.2.8-b130911.1802")
public class Cts {

  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String username;
  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String password;
  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String url;
  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String profile;
  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String mainzellisteUrl;
  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String mainzellisteApiKey;
  @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String searchIdType;


  /**
   * Gets the value of the username property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getUsername() {
    return username;
  }

  /**
   * Sets the value of the username property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setUsername(String value) {
    this.username = value;
  }


  /**
   * Gets the value of the password property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getPassword() {
    return password;
  }

  /**
   * Sets the value of the password property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setPassword(String value) {
    this.password = value;
  }

  /**
   * Gets the value of the depassword property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of the url property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setUrl(String value) {
    this.url = value;
  }

  /**
   * Gets the value of the profile property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getProfile() {
    return profile;
  }

  /**
   * Sets the value of the profile property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setProfile(String value) {
    this.profile = value;
  }

  /**
   * Gets the value of the mainzellisteUrl property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getMainzellisteUrl() {
    return mainzellisteUrl;
  }

  /**
   * Sets the value of the mainzellisteUrl property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setMainzellisteUrl(String value) {
    this.mainzellisteUrl = value;
  }

  /**
   * Gets the value of the mainzellisteApiKey property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getMainzellisteApiKey() {
    return mainzellisteApiKey;
  }

  /**
   * Sets the value of the mainzellisteApiKey property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setMainzellisteApiKey(String value) {
    this.mainzellisteApiKey = value;
  }

  /**
   * Gets the value of the searchIdType property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getSearchIdType() {
    return searchIdType;
  }

  /**
   * Sets the value of the searchIdType property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setSearchIdType(String value) {
    this.searchIdType = value;
  }

}
