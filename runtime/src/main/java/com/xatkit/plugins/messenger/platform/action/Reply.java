package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.Gson;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.MessengerUtils;
import com.xatkit.plugins.messenger.platform.entity.Messaging;
import com.xatkit.plugins.rest.platform.action.PostJsonRequestWithBody;
import fr.inria.atlanmod.commons.log.Log;
import lombok.val;
import org.apache.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public class Reply extends PostJsonRequestWithBody {
    private static final Gson gson = new Gson();

    /**
     * Constructs a POST Json request with a body parameter
     *
     * @param platform     the {@link MessengerPlatform} containing this action
     * @param context      the {@link StateContext} associated to this action
     * @param messaging  the body of the request
     */
    public Reply(MessengerPlatform platform, StateContext context, Messaging messaging) {
        super(platform, context, MessengerUtils.SEND_API_URL, null, null, gson.toJsonTree(messaging), generateHeaders(platform));
        Log.info("Sent {0}", gson.toJsonTree(messaging).toString());
    }

    private static Map<String, String> generateHeaders(MessengerPlatform platform) {
        val headers = new HashMap<String, String>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + platform.getAccessToken());
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        return headers;
    }
}
