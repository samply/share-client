package de.samply.share.client.crypt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CryptTest {

    public static final String TINK_KEY = "ewogICAgInByaW1hcnlLZXlJZCI6IDE2NTk5NjYwNTIsCiAgICAia2V5IjogW3sKICAgICAgICAia2V5RGF0YSI6IHsKICAgICAgICAgICAgInR5cGVVcmwiOiAidHlwZS5nb29nbGVhcGlzLmNvbS9nb29nbGUuY3J5cHRvLnRpbmsuQWVzU2l2S2V5IiwKICAgICAgICAgICAgImtleU1hdGVyaWFsVHlwZSI6ICJTWU1NRVRSSUMiLAogICAgICAgICAgICAidmFsdWUiOiAiRWtCMi9ZUHpPc2JUVFhmSHJaMm0rYmhjQm5zZWx5OElHM3MxZEZMeUVaeUo4blh5K3A0b0djYVk0ODczU0pLVDdZejM4Qjk4bW5HUnBzWUNWMks0QmJIaCIKICAgICAgICB9LAogICAgICAgICJvdXRwdXRQcmVmaXhUeXBlIjogIlRJTksiLAogICAgICAgICJrZXlJZCI6IDE2NTk5NjYwNTIsCiAgICAgICAgInN0YXR1cyI6ICJFTkFCTEVEIgogICAgfV0KfQ";
    public static final String PLAN_TEXT = "c368cf16-42d4-4fef-a726-9f21bd3f02f6";
    public static final String ENCREPTED_TEXT = "AWLxEmTLKEptthuKqYueuU0Ko_XaJuilITOMCcPIwgsiAasgSCg5Njaxn8qlsJQNyp3JWtSOkucI";


    @Test
    void encrypt() throws Exception {
        Crypt crypt = new Crypt(TINK_KEY);
        assertEquals(ENCREPTED_TEXT, crypt.encrypt(PLAN_TEXT));
    }

    @Test
    void decrypt() throws Exception {
        Crypt crypt = new Crypt(TINK_KEY);
        assertEquals(PLAN_TEXT, crypt.decrypt(ENCREPTED_TEXT));
    }
}
