
package de.samply.share.client.model.common;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Java class for urls complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="urls"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="shareUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="idmanagerUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/&gt;
 *         &lt;element name="idmanagerApiKey" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="ldmUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="mdrUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="directoryUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/&gt;
 *         &lt;element name="patientlistUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *         &lt;element name="projectPseudonymisationUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "urls", propOrder = {

})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
        comments = "JAXB RI v2.2.8-b130911.1802")
public class Urls {

  @XmlElement(namespace = "http://schema.samply.de/common", required = true)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String shareUrl;
  @XmlElement(namespace = "http://schema.samply.de/common", required = false)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String idmanagerUrl;
  @XmlElement(namespace = "http://schema.samply.de/common", required = true)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String idmanagerApiKey;
  @XmlElement(namespace = "http://schema.samply.de/common", required = true)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String ldmUrl;
  @XmlElement(namespace = "http://schema.samply.de/common", required = true)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String mdrUrl;
  @XmlElement(namespace = "http://schema.samply.de/common", required = true)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String directoryUrl;
  @XmlElement(namespace = "http://schema.samply.de/common", required = false)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String patientlistUrl;
  @XmlElement(namespace = "http://schema.samply.de/common", required = true)
  @XmlSchemaType(name = "anyURI")
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  protected String projectPseudonymisationUrl;

  /**
   * Gets the value of the shareUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getShareUrl() {
    return shareUrl;
  }

  /**
   * Sets the value of the shareUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setShareUrl(String value) {
    this.shareUrl = value;
  }

  /**
   * Gets the value of the idmanagerUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getIdmanagerUrl() {
    return idmanagerUrl;
  }

  /**
   * Sets the value of the idmanagerUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setIdmanagerUrl(String value) {
    this.idmanagerUrl = value;
  }

  /**
   * Gets the value of the idmanagerApiKey property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getIdmanagerApiKey() {
    return idmanagerApiKey;
  }

  /**
   * Sets the value of the idmanagerApiKey property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setIdmanagerApiKey(String value) {
    this.idmanagerApiKey = value;
  }

  /**
   * Gets the value of the ldmUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getLdmUrl() {
    return ldmUrl;
  }

  /**
   * Sets the value of the ldmUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setLdmUrl(String value) {
    this.ldmUrl = value;
  }

  /**
   * Gets the value of the mdrUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getMdrUrl() {
    return mdrUrl;
  }

  /**
   * Sets the value of the mdrUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setMdrUrl(String value) {
    this.mdrUrl = value;
  }

  /**
   * Gets the value of the directoryUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getDirectoryUrl() {
    return directoryUrl;
  }

  /**
   * Sets the value of the directoryUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setDirectoryUrl(String value) {
    this.directoryUrl = value;
  }

  /**
   * Gets the value of the patientlistUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getPatientlistUrl() {
    return patientlistUrl;
  }

  /**
   * Sets the value of the patientlistUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setPatientlistUrl(String value) {
    this.patientlistUrl = value;
  }

  /**
   * Gets the value of the projectPseudonymisationUrl property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public String getProjectPseudonymisationUrl() {
    return projectPseudonymisationUrl;
  }

  /**
   * Sets the value of the projectPseudonymisationUrl property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2021-01-04T10:57:27+01:00",
          comments = "JAXB RI v2.2.8-b130911.1802")
  public void setProjectPseudonymisationUrl(String value) {
    this.projectPseudonymisationUrl = value;
  }

}
