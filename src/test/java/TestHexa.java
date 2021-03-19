import com.google.crypto.tink.subtle.Hex;

import java.util.Base64;

public class TestHexa {

    public static void main(String[] args) {

      String id ="010503888604d2e37a4797b522e3fdcee6903fd9f1d494eca080ae66bab002f2f1392b07972bb6c41e525a88bf0f74e4da43811f99ffc0894107f46f5c68dd76f628ab15dd912f14d808f51c77463d6fd4c68988843ea42faa884c8dde467f438c9761389d8832fcce58";
      String decoded=  new String(Base64.getUrlEncoder().encode(Hex.decode(id)));
      System.out.println(decoded);
    }
}
