package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.Gson;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.MessengerUtils;
import com.xatkit.plugins.messenger.platform.entity.Messaging;
import com.xatkit.plugins.rest.platform.action.PostJsonRequestWithBody;
import lombok.val;
import org.apache.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * Reply action
 * Reply is sent without any delay unlike {@link MessageReply}
 *
 * @see MessageReply
 */
public class Reply extends PostJsonRequestWithBody {
    protected static final Gson gson = new Gson();

    /**
     * Constructs a Reply
     *
     * @param platform  the {@link MessengerPlatform} containing this action
     * @param context   the {@link StateContext} associated to this action
     * @param messaging the body of the request
     */
    public Reply(MessengerPlatform platform, StateContext context, Messaging messaging) {
        super(platform, context, MessengerUtils.SEND_API_URL, null, null, gson.toJsonTree(messaging), generateHeaders(platform));
    }

    /**
     * Generates headers for the POST request.
     *
     * @param platform the {@link MessengerPlatform} calling the action
     * @return generated headers
     */
    private static Map<String, String> generateHeaders(MessengerPlatform platform) {
        val headers = new HashMap<String, String>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + platform.getAccessToken());
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        return headers;
    }
}
