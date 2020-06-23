
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
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "username",
        "password",
        "url",
        "profile"
})
@XmlRootElement(name = "cts", namespace = "http://schema.samply.de/config/CtsInfo")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Cts {

    @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String username;
    @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String password;
    @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String url;
    @XmlElement(namespace = "http://schema.samply.de/config/CtsInfo", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String profile;


    /**
     * Gets the value of the username property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setUsername(String value) {
        this.username = value;
    }


    /**
     * Gets the value of the password property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the depassword property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the profile property.
     *
     * @return possible object is
     * {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-08-11T03:07:21+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setProfile(String value) {
        this.profile = value;
    }



}
