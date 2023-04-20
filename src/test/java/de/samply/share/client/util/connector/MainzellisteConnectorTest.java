package de.samply.share.client.util.connector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.samply.share.client.util.connector.MainzellisteConnector;
import org.junit.jupiter.api.Test;

class MainzellisteConnectorTest {

  @Test
  void testConcatenateDate() {
    MainzellisteConnector connectorMock = mock(
        de.samply.share.client.util.connector.MainzellisteConnector.class);
    String year = "2023";
    String month = "4";
    String day = "20";

    String expectedDate = "2023-04-20";
    when(connectorMock.concatenateDate(year, month, day)).thenCallRealMethod();
    String actualDate = connectorMock.concatenateDate(year, month, day);

    assertEquals(expectedDate, actualDate);

  }
}