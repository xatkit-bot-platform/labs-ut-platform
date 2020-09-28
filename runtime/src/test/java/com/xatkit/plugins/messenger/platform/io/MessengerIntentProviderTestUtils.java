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
    public static final String APP_SECRET = "TEST_APP_SECRET";
    public static final String CORRECT_CONTENT = "{\n" +
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
            "            \"id\":\"<PAGE_ID>\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"hello\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String CORRECT_CONTENT_MULTIPLE_MESSAGES = "{\n" +
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
            "            \"id\":\"<PAGE_ID>\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"hello\"\n" +
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
            "              \"text\":\"hello2\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String CORRECT_CONTENT_MULTIPLE_ENTRIES = "{\n" +
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
            "            \"id\":\"<PAGE_ID>\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"hello\"\n" +
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
            "            \"id\":\"<PAGE_ID>\"\n" +
            "          },\n" +
            "          \"message\":{\n" +
            "              \"text\":\"hello2\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String INCORRECT_CONTENT = "{\n" +
            "  \"object\":\"page\"\n" +
            "}";

    public static List<Header> generateHeaders(String content, String appSecret) throws InvalidKeyException, NoSuchAlgorithmException {
        return new ArrayList<Header>() {{
            add(new BasicHeader("X-Hub-Signature", "sha1=" + MessengerUtils.calculateRFC2104HMAC(content, appSecret)));
        }};
    }
}
