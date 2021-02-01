package de.samply.share.client.model.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the de.samply.share.client.model.common package. An ObjectFactory allows you to
 * programatically construct new instances of the Java representation for XML content. The Java
 * representation of XML content can consist of schema derived interfaces and classes representing
 * the binding of schema type definitions, element declarations and model groups.Factory methods for
 * each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  private static final QName _Operator_QNAME =
      new QName("http://schema.samply.de/common", "operator");
  private static final QName _Urls_QNAME =
      new QName("http://schema.samply.de/common", "urls");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes
   * for package: de.samply.share.client.model.common.
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link Urls }.
   */
  public Urls createUrls() {
    return new Urls();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link Urls }{@code >}}.
   */
  @XmlElementDecl(namespace = "http://schema.samply.de/common", name = "urls")
  public JAXBElement<Urls> createUrls(Urls value) {
    return new JAXBElement<Urls>(_Urls_QNAME, Urls.class, null, value);
  }

  /**
   * Create an instance of {@link Operator }.
   */
  public Operator createOperator() {
    return new Operator();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link Operator }{@code >}}.
   */
  @XmlElementDecl(namespace = "http://schema.samply.de/common", name = "operator")
  public JAXBElement<Operator> createOperator(Operator value) {
    return new JAXBElement<Operator>(_Operator_QNAME, Operator.class, null, value);
  }

  /**
   * Create an instance of {@link Bridgehead }.
   */
  public Bridgehead createBridgehead() {
    return new Bridgehead();
  }

  /**
   * Create an instance of {@link Cts }.
   */
  public Cts createCts() {
    return new Cts();
  }
}
