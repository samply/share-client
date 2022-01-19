package de.samply.share.client.model.centralsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class UploadStatsTest {

  @Test
  public void serializeAndDeserializeUploadStatsTest() throws JsonProcessingException {

    UploadStats uploadStats = createUploadStats();
    String stringUploadStats = serialize(uploadStats);
    UploadStats uploadStats2 = deserialize(stringUploadStats);

    assertEquals(uploadStats.getLastUploadTimestamp(), uploadStats2.getLastUploadTimestamp());

  }

  private UploadStats createUploadStats() {
    LocalDateTime localDateTime = LocalDateTime.now();
    return new UploadStats(localDateTime.toString());
  }

  private String serialize(UploadStats uploadStats) throws JsonProcessingException {
    ObjectMapper xmlMapper = new XmlMapper();
    return xmlMapper.writeValueAsString(uploadStats);
  }

  private UploadStats deserialize(String uploadStats) throws JsonProcessingException {
    ObjectMapper objectMapper = new XmlMapper();
    return objectMapper.readValue(uploadStats, UploadStats.class);
  }
  
}
