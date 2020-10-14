package com.xatkit.plugins.messenger.platform.io;

import com.xatkit.plugins.messenger.platform.MessengerUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MessengerIntentProviderTestUtils {
    public static final String SENDER_ID = "TEST78350350";
    public static final String RECIPIENT_PSID = "1254477777772919";
    public static final String MESSAGE_ID = "AG5Hz2Uq7tuwNEhXfYYKj8mJEM_QPpz5jdCK48PnKAjSdjfipqxqMvK8ma6AC8fplwlqLP_5cgXIbu7I3rBN0P";
    public static final String APP_SECRET = "TEST_APP_SECRET";
    public static final String MESSAGE_TEXT = "hello";
    public static final long WATERMARK = 1458668856253L;
    public static final String REACTION = "wow";
    public static final String EMOJI = "\uD83D\uDE2F";
    public static final String CORRECT_CONTENT =
            "{\n" +
            "  \"object\":\"page\",\n" +
            "  \"entry\":[\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"" + MESSAGE_TEXT + "\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String TEXTLESS_MESSAGE =
            "{\n" +
                    "  \"object\":\"page\",\n" +
                    "  \"entry\":[\n" +
                    "    {\n" +
                    "      \"id\":\"<PAGE_ID>\",\n" +
                    "      \"time\":1458692752478,\n" +
                    "      \"messaging\":[\n" +
                    "        {\n" +
                    "          \"sender\":{\n" +
                    "            \"id\":\"" + SENDER_ID + "\"\n" +
                    "          },\n" +
                    "          \"recipient\":{\n" +
                    "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
                    "          },\n" +
                    "          \"message\":{\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
    public static final String CORRECT_CONTENT_MULTIPLE_MESSAGES =
            "{\n" +
            "  \"object\":\"page\",\n" +
            "  \"entry\":[\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"" + MESSAGE_TEXT + "\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"<PAGE_ID>\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"" + MESSAGE_TEXT + "\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String CORRECT_CONTENT_MULTIPLE_ENTRIES =
            "{\n" +
            "  \"object\":\"page\",\n" +
            "  \"entry\":[\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"" + MESSAGE_TEXT + "\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"" + MESSAGE_TEXT + "\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String INCORRECT_CONTENT =
            "{\n" +
            "  \"object\":\"page\"\n" +
            "}";
    public static final String REACTION_MESSAGE =
            "{\n" +
            "  \"object\":\"page\",\n" +
            "  \"entry\":[\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "          },\n" +
            "          \"reaction\":{\n" +
            "            \"reaction\":\"" + REACTION + "\",\n" +
            "            \"emoji\":\"" + EMOJI + "\",\n" +
            "            \"action\":\"react\",\n" +
            "            \"mid\":\"" + MESSAGE_ID + "\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String UNREACTION_MESSAGE =
            "{\n" +
            "  \"object\":\"page\",\n" +
            "  \"entry\":[\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "        {\n" +
            "          \"sender\":{\n" +
            "            \"id\":\"" + SENDER_ID + "\"\n" +
            "          },\n" +
            "          \"recipient\":{\n" +
            "            \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "          },\n" +
            "          \"reaction\":{\n" +
            "            \"action\":\"unreact\",\n" +
            "            \"mid\":\"" + MESSAGE_ID + "\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String READ_MESSAGE =
            "{\n" +
            "  \"object\":\"page\",\n" +
            "  \"entry\":[\n" +
            "    {\n" +
            "      \"id\":\"<PAGE_ID>\",\n" +
            "      \"time\":1458692752478,\n" +
            "      \"messaging\":[\n" +
            "       {\n" +
            "           \"sender\":{\n" +
            "                \"id\":\"" + SENDER_ID + "\"\n" +
            "           },\n" +
            "           \"recipient\":{\n" +
            "               \"id\":\"" + RECIPIENT_PSID + "\"\n" +
            "           },\n" +
            "           \"timestamp\":1458668856463,\n" +
            "           \"read\":{\n" +
            "               \"watermark\":" + WATERMARK + "\n" +
            "           }\n" +
            "       }\n" +
            "     ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String DELIVERED_MESSAGE =
            "{\n" +
                    "  \"object\":\"page\",\n" +
                    "  \"entry\":[\n" +
                    "    {\n" +
                    "      \"id\":\"<PAGE_ID>\",\n" +
                    "      \"time\":1458692752478,\n" +
                    "      \"messaging\":[\n" +
                    "       {\n" +
                    "           \"sender\":{\n" +
                    "                \"id\":\"" + SENDER_ID + "\"\n" +
                    "           },\n" +
                    "           \"recipient\":{\n" +
                    "               \"id\":\"" + RECIPIENT_PSID + "\"\n" +
                    "           },\n" +
                    "           \"timestamp\":1458668856463,\n" +
                    "           \"delivery\":{\n" +
                    "               \"mids\":[" +
                    "                   \"" + MESSAGE_ID + "\"\n" +
                    "                   ],\n" +
                    "               \"watermark\":" + WATERMARK + "\n" +
                    "           }\n" +
                    "       }\n" +
                    "     ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    public static List<Header> generateHeaders(String content, String appSecret) throws InvalidKeyException, NoSuchAlgorithmException {
        return new ArrayList<Header>() {{
            add(new BasicHeader("X-Hub-Signature", "sha1=" + MessengerUtils.calculateRFC2104HMAC(content, appSecret)));
        }};
    }
}
