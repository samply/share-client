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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="centralsearch" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="decentralsearch" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="queryLanguage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "centralsearch",
    "decentralsearch",
    "queryLanguage"
})
@XmlRootElement(name = "bridgehead", namespace = "http://schema.samply.de/config/BridgeheadInfo")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
    comments = "JAXB RI v2.2.8-b130911.1802")
public class Bridgehead {

  @XmlElement(namespace = "http://schema.samply.de/config/BridgeheadInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String name;
  @XmlElement(namespace = "http://schema.samply.de/config/BridgeheadInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String centralsearch;
  @XmlElement(namespace = "http://schema.samply.de/config/BridgeheadInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String decentralsearch;
  @XmlElement(namespace = "http://schema.samply.de/config/BridgeheadInfo", required = true)
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  protected String queryLanguage;


  /**
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setName(String value) {
    this.name = value;
  }


  /**
   * Gets the value of the centralsearch property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getCentralsearch() {
    return centralsearch;
  }

  /**
   * Sets the value of the centralsearch property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setCentralsearch(String value) {
    this.centralsearch = value;
  }

  /**
   * Gets the value of the decentralsearch property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getDecentralsearch() {
    return decentralsearch;
  }

  /**
   * Sets the value of the decentralsearch property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setDecentralsearch(String value) {
    this.decentralsearch = value;
  }

  /**
   * Gets the value of the queryLanguage property.
   *
   * @return possible object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public String getQueryLanguage() {
    return queryLanguage;
  }

  /**
   * Sets the value of the queryLanguage property.
   *
   * @param value allowed object is {@link String }
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00",
      comments = "JAXB RI v2.2.8-b130911.1802")
  public void setQueryLanguage(String value) {
    this.queryLanguage = value;
  }


}
