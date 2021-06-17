package de.samply.share.client.crypt;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.daead.DeterministicAeadConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.ConfigurationException;

/**
 * Symmetric encryption and decryption.
 */
public class Crypt {

  private static DeterministicAead primitive;

  public String encrypt(String plainText) throws GeneralSecurityException {
    return Base64.encodeBase64URLSafeString(
        primitive.encryptDeterministically(plainText.getBytes(StandardCharsets.UTF_8), null));
  }

  public String decrypt(String plainText)
      throws GeneralSecurityException {
    return new String(primitive.decryptDeterministically(Base64.decodeBase64(plainText), null),
        StandardCharsets.UTF_8);
  }

  /**
   * Create a primitive for the encryption and decryption. If the keySet is not existing then it
   * will be created.
   */
  public Crypt(String key) throws GeneralSecurityException, ConfigurationException, IOException {
    init(key);
  }


  private void init(String key)
      throws ConfigurationException, GeneralSecurityException, IOException {
    if ("".equals(key)) {
      throw new ConfigurationException("No configuration for encryption found");
    }
    String keyEncoded = new String(Base64.decodeBase64(key.getBytes()));
    DeterministicAeadConfig.register();
    KeysetHandle keysetHandle = CleartextKeysetHandle.read(
        JsonKeysetReader.withString(keyEncoded));
    primitive = keysetHandle.getPrimitive(DeterministicAead.class);
  }
}
