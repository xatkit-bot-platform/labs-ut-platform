package com.xatkit.plugins.messenger.platform;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class MessengerUtils {
    public static final String MESSENGER_CONTEXT = "xatkit.messenger.";

    // CONSTANTS
    public static final String WEBHOOK_URI = "/messenger/webhook";
    public static final String SEND_API_URL = "https://graph.facebook.com/v8.0/me/messages";
    public static final String ATTACHMENT_UPLOAD_API_URL = "https://graph.facebook.com/v8.0/me/message_attachments";

    // PLATFORM CONFIGURATION KEYS
    public static final String VERIFY_TOKEN_KEY = MESSENGER_CONTEXT + "verify_token";
    public static final String ACCESS_TOKEN_KEY = MESSENGER_CONTEXT + "access_token";
    public static final String APP_SECRET_KEY = MESSENGER_CONTEXT + "app_secret";

    public static final String INTENT_FROM_POSTBACK = MESSENGER_CONTEXT + "intent_from_postback";
    public static final String USE_REACTION_TEXT = MESSENGER_CONTEXT + "use_reaction_text";

    public static final String INTENT_FROM_REACTION = MESSENGER_CONTEXT + "intent_from_reaction";
    public static final String USE_POSTBACK_TITLE_TEXT = MESSENGER_CONTEXT + "use_title_text";

    public static final String HANDLE_REACTIONS_KEY = MESSENGER_CONTEXT + "handle_reactions";
    public static final String HANDLE_DELIVERIES_KEY = MESSENGER_CONTEXT + "handle_deliveries";
    public static final String HANDLE_READ_KEY = MESSENGER_CONTEXT + "handle_read";

    // EXPERIMENTAL FEATURES
    public static final String AUTO_MARK_SEEN_KEY = MESSENGER_CONTEXT + "auto_seen";
    public static final String NATURALIZE_TEXT = MESSENGER_CONTEXT + "naturalize_text";

    // INTENT PLATFORM DATA KEYS
    public static final String RAW_TEXT_KEY = MESSENGER_CONTEXT + "raw_text";
    public static final String MESSAGE_ID_KEY = MESSENGER_CONTEXT + "mid";
    public static final String MESSAGE_IDS_KEY = MESSENGER_CONTEXT + "mids";
    public static final String WATERMARK_KEY = MESSENGER_CONTEXT + "watermark";

    public static final String POSTBACK_CONTEXT = MESSENGER_CONTEXT + "postback.";
    public static final String POSTBACK_TITLE_KEY = POSTBACK_CONTEXT + "title";
    public static final String POSTBACK_PAYLOAD_KEY = POSTBACK_CONTEXT + "payload";
    public static final String POSTBACK_REFERRAL_CONTEXT = POSTBACK_CONTEXT + "referral.";
    public static final String POSTBACK_REFERRAL_REF_KEY = POSTBACK_REFERRAL_CONTEXT + "ref";
    public static final String POSTBACK_REFERRAL_SOURCE_KEY = POSTBACK_REFERRAL_CONTEXT + "source";
    public static final String POSTBACK_REFERRAL_TYPE_KEY = POSTBACK_REFERRAL_CONTEXT + "type";

    public static final String EMOJI_KEY = MESSENGER_CONTEXT + "emoji";
    public static final String REACTION_KEY = MESSENGER_CONTEXT + "reaction";


    /**
     * Calculates RFC2105HMAC from data using the key
     *
     * @param data data to encode
     * @param key  key to encode the data with
     * @return the data encoded with the key.
     */
    public static String calculateRFC2104HMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return new String(Hex.encodeHex(mac.doFinal(data.getBytes())));
    }

    /**
     * If one adds dialogflow to the bot, dialogflow will add a prefix to the context id.
     * This method will remove the prefix in order for this context id to be usable.
     *
     * @param contextIdWithDialogflow the context id from dialogflow
     * @return context id with the prefix removed.
     */
    public static String extractContextId(String contextIdWithDialogflow) {
        String[] senderIdSplit = contextIdWithDialogflow.split("/"); //This removes the useless part and leaves only the id
        return senderIdSplit[senderIdSplit.length - 1]; //A better solution is more than welcome

    }
}
