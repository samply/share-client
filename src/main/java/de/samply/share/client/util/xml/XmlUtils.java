package de.samply.share.client.util.xml;

import de.samply.share.client.util.connector.exception.XmlPareException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Utils for processing the XML content.
 */
public class XmlUtils {
  public static final boolean EMPTY_NULL_CHECK = true;
  public static final boolean NO_EMPTY_NULL_CHECK = false;
  public static final String IDENTIFYING_DATA_ELEMENT = "identifying-data";
  public static final String PATIENT_ELEMENT = "patient";
  public static final String NACHNAME_ELEMENT = "nachname";
  public static final String VORNAME_ELEMENT = "vorname";
  public static final String GEBURTSNAME_ELEMENT = "geburtsname";
  public static final String GEBURTSTAG_ELEMENT = "geburtstag";
  public static final String GEBURTSMONAT_ELEMENT = "geburtsmonat";
  public static final String GEBURTSJAHR_ELEMENT = "geburtsjahr";
  public static final String VERSICHERUNGSNUMMER_ELEMENT = "versicherungsnummer";
  public static final String ADRESSEPLZ_ELEMENT = "adresseplz";
  public static final String ADRESSESTADT_ELEMENT = "adressestadt";
  public static final String ADRESSESTRASSE_ELEMENT = "adressestrasse";
  public static final String MEDICAL_DATA_ELEMENT = "medical-data";
  public static final String TOKEN_ELEMENT = "token";
  public static final String BIRTHDATE_ELEMENT = "birthdate";

  /**
 * xml utils.
 *
 * @param inputStream inputStream
 * @return Document
 * @throws XmlPareException XmlPareException
 * @throws IOException IOException
  */

  public Document domBuilder(InputStream inputStream)
          throws IOException, XmlPareException {
    final DocumentBuilder dBuilder;
    try {
      dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document xmlDoc = dBuilder.parse(inputStream);
      xmlDoc.getDocumentElement().normalize();
      return xmlDoc;
    } catch (ParserConfigurationException | SAXException e) {
      throw new XmlPareException("Error while parsing  the xml content: "  + e.getMessage(), e);
    }
  }

  /**
     * Get a value from the xml content.
     *
     * @param document document.
     * @param elementName elementName.
     * @return value
     */

  public String readValueFromXml(Document document, String elementName, boolean safeMode)
      throws XmlPareException {
    String value = "";
    Element element = (Element) document.getElementsByTagName(elementName).item(0);
    try {
      if (safeMode) {
        if (element == null || element.getTextContent() == null || element.getTextContent()
            .isEmpty()) {
          throw new XmlPareException(" XML element doesn't exist or empty: " + elementName);
        } else {
          value = element.getTextContent();
        }
      } else if (element != null && element.getTextContent() != null && !element.getTextContent()
          .isEmpty()) {
        value = element.getTextContent();
      }
    } catch (XmlPareException e) {
      throw new XmlPareException(" XML element doesn't exist or empty: " + elementName);
    }
    return value.trim();
  }

  /**
   * Transform a xml document to String.
   *
   * @param document xml document
   * @return String
   * @throws XmlPareException XmlPareException
   */

  public String xmlDocToString(Document document) throws XmlPareException {
    String xmlString;
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(document);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.transform(source, result);
      xmlString = writer.getBuffer().toString();
    } catch (TransformerException e) {
      throw new XmlPareException(" Error while transforming XML document: " + e.getMessage());
    }
    return xmlString;
  }

  /**
   * Remove children of a specific node.
   *
   * @param node xml node.
   */

  public void removeNodeChildren(Node node) {
    NodeList children = node.getChildNodes();
    for (int i = 0, length = children.getLength(); i < length; i++) {
      node.removeChild(children.item(i));
    }
  }
}
