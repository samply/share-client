package de.samply.share.client.crypt;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.config.TinkConfig;
import de.samply.config.util.FileFinderUtil;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import org.apache.commons.codec.binary.Base64;

public class Crypt {

  private static Aead primitive;

  public String encrypt(String plainText) throws GeneralSecurityException {
    return Base64.encodeBase64URLSafeString(
        primitive.encrypt(plainText.getBytes(StandardCharsets.UTF_8), null));
  }

  public String decrypt(String plainText)
      throws GeneralSecurityException {
    return new String(primitive.decrypt(Base64.decodeBase64(plainText), null),
        StandardCharsets.UTF_8);
  }

  /**
   * Create a primitive for the encryption and decryption. If the keySet is not existing then it
   * will be created.
   */
  public Crypt() throws GeneralSecurityException, IOException {
    init();
  }


  private void init() throws GeneralSecurityException, IOException {
    String keysetFilename = "cts_symmetric_key.json";
    KeysetHandle keysetHandle = null;
    try {
      TinkConfig.register();
      File f = new File(FileFinderUtil.findFile(
          keysetFilename, ProjectInfo.INSTANCE.getProjectName(),
          System.getProperty("catalina.base") + File.separator + "conf")
          .getAbsolutePath());
      keysetHandle = CleartextKeysetHandle.read(
          JsonKeysetReader.withFile(f));
    } catch (FileNotFoundException e) {
      String path = SamplyShareUtils.addTrailingFileSeparator(
          System.getProperty("catalina.base") + File.separator + "conf");
      keysetHandle = KeysetHandle.generateNew(
          AesGcmKeyManager.aes128GcmTemplate());
      CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(
          new File(path + keysetFilename)));
    }
    primitive = keysetHandle.getPrimitive(Aead.class);
  }
}
