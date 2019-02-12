
package de.samply.share.client.model.common;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="longname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dktkid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="monitor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="interval" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="centralsearch" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decentralsearch" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="updateserver" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "longname",
    "dktkid",
    "monitor",
    "interval",
    "centralsearch",
    "decentralsearch",
    "updateserver"
})
@XmlRootElement(name = "bridgehead", namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Bridgehead {

    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String name;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String longname;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String dktkid;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String monitor;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String interval;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String centralsearch;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String decentralsearch;
    @XmlElement(namespace = "http://schema.samply.de/config/DKTKBridgeheadInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String updateserver;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the longname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getLongname() {
        return longname;
    }

    /**
     * Sets the value of the longname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setLongname(String value) {
        this.longname = value;
    }

    /**
     * Gets the value of the dktkid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getDktkid() {
        return dktkid;
    }

    /**
     * Sets the value of the dktkid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setDktkid(String value) {
        this.dktkid = value;
    }

    /**
     * Gets the value of the monitor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getMonitor() {
        return monitor;
    }

    /**
     * Sets the value of the monitor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMonitor(String value) {
        this.monitor = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setInterval(String value) {
        this.interval = value;
    }

    /**
     * Gets the value of the centralsearch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getCentralsearch() {
        return centralsearch;
    }

    /**
     * Sets the value of the centralsearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setCentralsearch(String value) {
        this.centralsearch = value;
    }

    /**
     * Gets the value of the decentralsearch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getDecentralsearch() {
        return decentralsearch;
    }

    /**
     * Sets the value of the decentralsearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setDecentralsearch(String value) {
        this.decentralsearch = value;
    }

    /**
     * Gets the value of the updateserver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getUpdateserver() {
        return updateserver;
    }

    /**
     * Sets the value of the updateserver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setUpdateserver(String value) {
        this.updateserver = value;
    }

}
