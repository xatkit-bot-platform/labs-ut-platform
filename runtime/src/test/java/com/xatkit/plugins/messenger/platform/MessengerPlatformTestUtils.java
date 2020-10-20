package com.xatkit.plugins.messenger.platform;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MessengerPlatformTestUtils {
    public static final String VERIFY_TOKEN = "TEST TOKEN";
    public static final String CHALLENGE = "CHALLENGE_ACCEPTED";

    public static final List<NameValuePair> VALIDATION_PARAMS = new ArrayList<NameValuePair>() {{
        add(new BasicNameValuePair("hub.challenge", CHALLENGE));
        add(new BasicNameValuePair("hub.verify_token", VERIFY_TOKEN));
        add(new BasicNameValuePair("hub.mode", "subscribe"));
    }};

    public static final List<NameValuePair> INCORRECT_TOKEN_VALIDATION_PARAMS = new ArrayList<NameValuePair>() {{
        add(new BasicNameValuePair("hub.challenge", CHALLENGE));
        add(new BasicNameValuePair("hub.verify_token", VERIFY_TOKEN + "x"));
        add(new BasicNameValuePair("hub.mode", "subscribe"));
    }};

    public static final List<NameValuePair> INCORRECT_TYPE_VALIDATION_PARAMS = new ArrayList<NameValuePair>() {{
        add(new BasicNameValuePair("hub.challenge", CHALLENGE));
        add(new BasicNameValuePair("hub.verify_token", VERIFY_TOKEN));
        add(new BasicNameValuePair("hub.mode", "not_subscribe"));
    }};
}
