package com.xatkit.plugins.messenger.platform.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MessengerIntentProviderTestUtils {
    private static final JsonParser jsonParser = new JsonParser();
    public static final String senderId = "TEST78350350";

    public enum Requests {
        CORRECT_CONTENT("{\n" +
                "  \"object\":\"page\",\n" +
                "  \"entry\":[\n" +
                "    {\n" +
                "      \"id\":\"<PAGE_ID>\",\n" +
                "      \"time\":1458692752478,\n" +
                "      \"messaging\":[\n" +
                "        {\n" +
                "          \"sender\":{\n" +
                "            \"id\":\"" + senderId + "\"\n" +
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
                "}"),
        CORRECT_CONTENT_MULTIPLE_MESSAGES("{\n" +
                "  \"object\":\"page\",\n" +
                "  \"entry\":[\n" +
                "    {\n" +
                "      \"id\":\"<PAGE_ID>\",\n" +
                "      \"time\":1458692752478,\n" +
                "      \"messaging\":[\n" +
                "        {\n" +
                "          \"sender\":{\n" +
                "            \"id\":\"" + senderId + "\"\n" +
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
                "            \"id\":\"" + senderId + "\"\n" +
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
                "}"),

        CORRECT_CONTENT_MULTIPLE_ENTRIES("{\n" +
                "  \"object\":\"page\",\n" +
                "  \"entry\":[\n" +
                "    {\n" +
                "      \"id\":\"<PAGE_ID>\",\n" +
                "      \"time\":1458692752478,\n" +
                "      \"messaging\":[\n" +
                "        {\n" +
                "          \"sender\":{\n" +
                "            \"id\":\"" + senderId + "\"\n" +
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
                "            \"id\":\"" + senderId + "\"\n" +
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
                "}"),
        INCORRECT_CONTENT("{\n" +
                "  \"object\":\"page\"\n" +
                "}");

        Requests(String request) {
            this.request = request;
        }

        private final String request;

        public JsonElement getJsonElement() {
            return jsonParser.parse(request);
        }
    }
}
