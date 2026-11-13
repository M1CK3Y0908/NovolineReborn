package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager {
   private static final Logger LOGGER = LogManager.getLogger();

   public static Cipher createNetCipherInstance(int opMode, Key key) {
      try {
         Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
         cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
         return cipher;
      } catch (GeneralSecurityException generalSecurityException) {
         throw new RuntimeException(generalSecurityException);
      }
   }

   private static byte[] digestOperation(String algorithm, byte[]... data) {
      try {
         MessageDigest instance = MessageDigest.getInstance(algorithm);

         for (byte[] bArray : data) {
            instance.update(bArray);
         }

         return instance.digest();
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         noSuchAlgorithmException.printStackTrace();
         return null;
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
         keyPairGenerator.initialize(1024);
         return keyPairGenerator.generateKeyPair();
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         noSuchAlgorithmException.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
      try {
         return digestOperation("SHA-1", serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
         unsupportedEncodingException.printStackTrace();
         return null;
      }
   }

   public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted) {
      return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
   }

   private static Cipher createTheCipherInstance(int opMode, String transformation, Key key) {
      try {
         Cipher cipher = Cipher.getInstance(transformation);
         cipher.init(opMode, key);
         return cipher;
      } catch (InvalidKeyException invalidKeyException) {
         invalidKeyException.printStackTrace();
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         noSuchAlgorithmException.printStackTrace();
      } catch (NoSuchPaddingException noSuchPaddingException) {
         noSuchPaddingException.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static SecretKey createNewSharedKey() {
      try {
         KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
         keyGenerator.init(128);
         return keyGenerator.generateKey();
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         throw new Error(noSuchAlgorithmException);
      }
   }

   private static byte[] cipherOperation(int opMode, Key key, byte[] data) {
      try {
         return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
      } catch (IllegalBlockSizeException illegalBlockSizeException) {
         illegalBlockSizeException.printStackTrace();
      } catch (BadPaddingException badPaddingException) {
         badPaddingException.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   public static PublicKey decodePublicKey(byte[] encodedKey) {
      try {
         X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedKey);
         return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      } catch (InvalidKeySpecException invalidKeySpecException) {
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static byte[] encryptData(Key key, byte[] data) {
      return cipherOperation(1, key, data);
   }

   public static byte[] decryptData(Key key, byte[] data) {
      return cipherOperation(2, key, data);
   }
}
