package sm.playground.simulator.kisapi.token.security;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesCbcEncryptor {

    @Value("${security.encryption.key}")
    private String key; // 32바이트 (AES-256)

    private SecretKeySpec secretKeySpec;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    @PostConstruct
    public void init() {
        if (key.length() != 32) {
            throw new IllegalArgumentException("Encryption key must be 32 bytes (256 bits) / " + key.length());
        }
        this.secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = generateRandomIv();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 암호문 = IV + 암호화된 데이터
            byte[] ivAndCipherText = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, ivAndCipherText, 0, iv.length);
            System.arraycopy(encrypted, 0, ivAndCipherText, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(ivAndCipherText);
        } catch (Exception e) {
            throw new RuntimeException("🔐 CBC encryption failed", e);
        }
    }

    public String decrypt(String base64CipherText) {
        try {
            byte[] ivAndCipherText = Base64.getDecoder().decode(base64CipherText);

            byte[] iv = new byte[IV_SIZE];
            byte[] cipherText = new byte[ivAndCipherText.length - IV_SIZE];

            System.arraycopy(ivAndCipherText, 0, iv, 0, IV_SIZE);
            System.arraycopy(ivAndCipherText, IV_SIZE, cipherText, 0, cipherText.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(cipherText);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("🔓 CBC decryption failed", e);
        }
    }

    private byte[] generateRandomIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
