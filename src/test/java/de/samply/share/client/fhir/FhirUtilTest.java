package de.samply.share.client.fhir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import javax.ws.rs.core.MediaType;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FhirUtilTest {

  public static final String BUNDLE_STRING = "kaf20g.";
  public static final MediaType MEDIA_TYPE_JSON = new MediaType("application", "json");
  public static final String RESULT_STRING = "result-1442";
  @Mock
  private FhirContext fhirContext;

  @Mock
  private IParser jsonParser;

  @Mock
  private IParser xmlParser;

  @InjectMocks
  private FhirUtil fhirUtil;

  @Test
  void parseBundleResource_default() throws FhirParseException {
    when(fhirContext.newXmlParser()).thenReturn(xmlParser);
    Bundle expectedBundle = new Bundle();
    when(xmlParser.parseResource(BUNDLE_STRING)).thenReturn(expectedBundle);

    Bundle bundle = fhirUtil.parseBundleResource(BUNDLE_STRING, new MediaType());

    assertSame(expectedBundle,bundle);
  }

  @Test
  void parseBundleResource_json() throws FhirParseException {
    when(fhirContext.newJsonParser()).thenReturn(jsonParser);
    Bundle expectedBundle = new Bundle();
    when(jsonParser.parseResource(BUNDLE_STRING)).thenReturn(expectedBundle);

    Bundle bundle = fhirUtil.parseBundleResource(BUNDLE_STRING, MEDIA_TYPE_JSON);

    assertSame(expectedBundle,bundle);
  }

  @Test
  void parseBundleResource_configurationException() {
    when(fhirContext.newJsonParser()).thenReturn(jsonParser);
    ConfigurationException configurationException = new ConfigurationException();
    when(jsonParser.parseResource(BUNDLE_STRING)).thenThrow(configurationException);

    FhirParseException exception = assertThrows(FhirParseException.class, ()->fhirUtil.parseBundleResource(BUNDLE_STRING,
        MEDIA_TYPE_JSON));

    assertEquals("Error while parsing a json bundle.", exception.getMessage());
    assertEquals(configurationException, exception.getCause());
  }

  @Test
  void parseBundleResource_dataFormatException() {
    when(fhirContext.newJsonParser()).thenReturn(jsonParser);
    DataFormatException dataFormatException = new DataFormatException();
    when(jsonParser.parseResource(BUNDLE_STRING)).thenThrow(dataFormatException);

    FhirParseException exception = assertThrows(FhirParseException.class, ()->fhirUtil.parseBundleResource(BUNDLE_STRING,
        MEDIA_TYPE_JSON));

    assertEquals("Error while parsing a json bundle.", exception.getMessage());
    assertEquals(dataFormatException, exception.getCause());
  }

  @Test
  void encodeResourceToJson() {
    when(fhirContext.newJsonParser()).thenReturn(jsonParser);
    Bundle bundle = new Bundle();
    when(jsonParser.encodeResourceToString(bundle)).thenReturn(RESULT_STRING);

    String result = fhirUtil.encodeResourceToJson(bundle);

    assertEquals(RESULT_STRING,result);
  }

  @Test
  void encodeResourceToJson_dataFormatException() {
    when(fhirContext.newJsonParser()).thenReturn(jsonParser);
    DataFormatException dataFormatException = new DataFormatException();
    Bundle bundle = new Bundle();
    when(jsonParser.encodeResourceToString(bundle)).thenThrow(dataFormatException);

    FhirEncodeException exception = assertThrows(FhirEncodeException.class, ()->fhirUtil.encodeResourceToJson(bundle));

    assertEquals("Error while encoding a bundle to json.", exception.getMessage());
    assertEquals(dataFormatException, exception.getCause());
  }
}
