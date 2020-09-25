package com.xatkit.plugins.messenger.platform;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.core.server.*;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.rest.platform.RestPlatform;
import lombok.NonNull;
import fr.inria.atlanmod.commons.log.Log;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.StringEntity;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

// TODO: Add javadocs
/**
 * A {@link RuntimePlatform} class that connects and interacts with the Messenger API.
 */
public class MessengerPlatform extends RestPlatform {
    private String verifyToken;
    private String accessToken;
    private String appSecret;

    @Override
    public void start(@NonNull XatkitBot xatkitBot, @NonNull Configuration configuration) {
        verifyToken = requireNonNull(configuration.getString(MessengerUtils.VERIFY_TOKEN_KEY));
        accessToken = requireNonNull(configuration.getString(MessengerUtils.ACCESS_TOKEN_KEY));
        appSecret = requireNonNull(configuration.getString(MessengerUtils.APP_SECRET_KEY));
        super.start(xatkitBot, configuration);

        xatkitBot.getXatkitServer().registerRestEndpoint(HttpMethod.GET, "/messenger/webhook",
                RestHandlerFactory.createEmptyContentRestHandler((headers, params, content) -> {
                    val mode = requireNonNull(HttpUtils.getParameterValue("hub.mode", params), "Missing mode");
                    val token = requireNonNull(HttpUtils.getParameterValue("hub.verify_token", params), "Missing token");
                    val challenge = requireNonNull(HttpUtils.getParameterValue("hub.challenge", params), "Missing challenge");
                    if (!mode.equals("subscribe")) {
                        throw new RestHandlerException(403, "Mode is not 'subscribe'");
                    }
                    if (!token.equals(verifyToken)) {
                        throw new RestHandlerException(403, "Token does not match verify token.");
                    }
                    return new StringEntity(challenge, "UTF-8");
                }));
    }

    public void reply(@NonNull StateContext context, @NonNull String message) {
        val senderId = context.getContextId();
        Log.debug("REPLYING TO: {0}", senderId);
        val body = new JsonObject();

        val recipent = new JsonObject();
        recipent.add("id", new JsonPrimitive(senderId));
        body.add("recipient", recipent);

        val messageObject = new JsonObject();
        messageObject.add("text", new JsonPrimitive(message));
        body.add("message", messageObject);

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");

        val response = postJsonRequestWithBody(
                context,
                "https://graph.facebook.com/v8.0/me/messages",
                new HashMap<>(),
                new HashMap<>(),
                body,
                headers);

        Log.debug("REPLY RESPONSE STATUS: {0} {1}\n BODY: {2}", response.getStatus(), response.getStatusText(),response.getBody().toString());
    }

    public String getAppSecret() {
        return appSecret;
    }
}
