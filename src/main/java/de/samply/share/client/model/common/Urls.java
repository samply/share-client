
package de.samply.share.client.model.common;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for urls complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="urls">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="shareUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="idmanagerUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="centraxxUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="mdrUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="mainzellisteUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="mdrUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "urls", namespace = "http://schema.samply.de/common", propOrder = {

})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Urls {

    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String shareUrl;
    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String idmanagerUrl;
    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String centraxxUrl;
    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String mdrUrl;
    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String mainzellisteUrl;
    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String ctsUrl;

    /**
     * Gets the value of the shareUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setIdmanagerUrl(String value) {
        this.idmanagerUrl = value;
    }

    /**
     * Gets the value of the centraxxUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getCentraxxUrl() {
        return centraxxUrl;
    }

    /**
     * Sets the value of the centraxxUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setCentraxxUrl(String value) {
        this.centraxxUrl = value;
    }

    /**
     * Gets the value of the mdrUrl property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getMdrUrl() {
        return mdrUrl;
    }

    /**
     * Sets the value of the mdrUrl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMdrUrl(String value) {
        this.mdrUrl = value;
    }
    /**
     * Gets the value of the ctsUrl property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getCtsUrl() {
        return ctsUrl;
    }

    /**
     * Sets the value of the ctsUrl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setCtsUrl(String value) {
        this.ctsUrl = value;
    }
    /**
     * Gets the value of the mainzellisteUrl property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getMainzellisteUrl() {
        return mainzellisteUrl;
    }

    /**
     * Sets the value of the mainzellisteUrl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMainzellisteUrl(String value) {
        this.mainzellisteUrl = value;
    }
}
