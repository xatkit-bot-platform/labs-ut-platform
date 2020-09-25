package com.xatkit.plugins.messenger.platform;

import com.xatkit.core.platform.io.WebhookEventProvider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;


public class MessengerUtils {
    public static final String VERIFY_TOKEN_KEY = "xatkit.messenger.verify_token";
    public static final String ACCESS_TOKEN_KEY = "xatkit.messenger.access_token";
    public static final String APP_SECRET_KEY = "xatkit.messenger.app_secret";


    /**
     * Methods to help validate the webhook events, which use encodings HmacSHA1 encodings for identification
     * See <a href="https://gist.github.com/aqnouch/8dd60812c2ef938bc73a947dabcfd506">code origin</a>
     */
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));

    }
}
