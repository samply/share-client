
package de.samply.share.client.model.common;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;


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
 *         &lt;element name="ldmUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
    protected String ldmUrl;
    @XmlElement(namespace = "http://schema.samply.de/common", required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String mdrUrl;

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
     * Gets the value of the ldmUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setLdmUrl(String value) {
        this.ldmUrl = value;
    }

    /**
     * Gets the value of the ldmUrl property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getMdrUrl() {
        return mdrUrl;
    }

    /**
     * Sets the value of the ldmUrl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMdrUrl(String value) {
        this.mdrUrl = value;
    }
}
