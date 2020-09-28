package com.xatkit.plugins.messenger.platform;

import com.xatkit.core.platform.io.WebhookEventProvider;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class MessengerUtils {
    public static final String VERIFY_TOKEN_KEY = "xatkit.messenger.verify_token";
    public static final String ACCESS_TOKEN_KEY = "xatkit.messenger.access_token";
    public static final String APP_SECRET_KEY = "xatkit.messenger.app_secret";
    public static final String WEBHOOK_URI = "/messenger/webhook";

    public static String calculateRFC2104HMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return new String(Hex.encodeHex(mac.doFinal(data.getBytes())));
    }
}
