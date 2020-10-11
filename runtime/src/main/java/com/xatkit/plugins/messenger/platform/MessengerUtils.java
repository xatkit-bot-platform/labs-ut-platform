package com.xatkit.plugins.messenger.platform;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class MessengerUtils {
    public static final String MESSENGER_CONTEXT = "xatkit.messenger.";
    public static final String VERIFY_TOKEN_KEY = MESSENGER_CONTEXT + "verify_token";
    public static final String ACCESS_TOKEN_KEY = MESSENGER_CONTEXT + "access_token";
    public static final String APP_SECRET_KEY = MESSENGER_CONTEXT + "app_secret";
    public static final String HANDLE_REACTIONS_KEY = MESSENGER_CONTEXT + "handle_reactions";
    public static final String HANDLE_DELIVERIES_KEY = MESSENGER_CONTEXT + "handle_deliveries";
    public static final String HANDLE_READ_KEY = MESSENGER_CONTEXT + "handle_read";
    public static final String AUTO_MARK_SEEN_KEY = MESSENGER_CONTEXT + "auto_seen";
    public static final String RAW_TEXT_KEY = "raw_text";
    public static final String MESSAGE_ID_KEY = "mid";
    public static final String MESSAGE_IDS_KEY = "mids";
    public static final String WATERMARK_KEY = "watermark";
    public static final String EMOJI_KEY = "emoji";
    public static final String REACTION_KEY = "reaction";
    public static final String WEBHOOK_URI = "/messenger/webhook";
    public static final String SEND_API_URL = "https://graph.facebook.com/v8.0/me/messages";


    public static String calculateRFC2104HMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return new String(Hex.encodeHex(mac.doFinal(data.getBytes())));
    }
}
