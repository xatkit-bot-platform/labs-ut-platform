package com.xatkit.plugins.messenger.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.core.server.*;
import com.xatkit.execution.impl.ExecutionFactoryImpl;
import com.xatkit.plugins.rest.platform.RestPlatform;
import com.xatkit.plugins.rest.platform.RestPlatformConfiguration;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.nonNull;

// TODO: Add javadocs
/**
 * A {@link RuntimePlatform} class that connects and interacts with the Messenger API.
 */
public class MessengerPlatform extends RestPlatform {
    private String verifyToken;
    private String accessToken;
    private RestPlatformConfiguration restPlatformConfiguration;


    @Override
    public void start(XatkitCore xatkitCore, Configuration configuration) {
        verifyToken = requireNonNull(configuration.getString(MessengerUtils.VERIFY_TOKEN_KEY));
        accessToken = requireNonNull(configuration.getString(MessengerUtils.ACCESS_TOKEN_KEY));
        restPlatformConfiguration = new RestPlatformConfiguration(configuration);

        xatkitCore.getXatkitServer().registerRestEndpoint(HttpMethod.GET, "/messenger/webhook",
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

        // TODO: add validation of post request https://developers.facebook.com/docs/messenger-platform/webhook#security
        // TODO: could use some refactoring. get entity class from json
        // TODO: make this into an event and/or intent provider.
        xatkitCore.getXatkitServer().registerRestEndpoint(HttpMethod.POST, "/messenger/webhook", new MessengerRestHandler() {

            @Override
            public HttpEntity handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params, @Nullable JsonElement content) throws RestHandlerException {
                try {
                    checkArgument(nonNull(content), "Missing content.");

                    requireNonNull(content.getAsJsonObject().get("entry"), "Missing entry.")
                            .getAsJsonArray()
                            .iterator()
                            .forEachRemaining(entry -> handleWebhookEntry(entry));

                    // TODO: add proper logging
                    System.out.println("SENDING OK");
                    return new StringEntity("RECEIVED", "UTF-8");
                } catch (NullPointerException | IllegalStateException | IllegalArgumentException e) {
                    throw new RestHandlerException(403, e.getMessage());
                }
            }
        });
    }

    // TODO: this needs refactoring as well
    private void handleWebhookEntry(JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            return;
        }
        val jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.has("messaging")) {
            return;
        }
        val messaging = jsonObject.get("messaging").getAsJsonArray();
        messaging.iterator().forEachRemaining(message -> {
            val messageJsonObject = message.getAsJsonObject();
            val sender = requireNonNull(messageJsonObject.get("sender"), "Message has no sender");
            val id = requireNonNull(sender.getAsJsonObject().get("id"), "Sender has no id");

            // TODO: Repliest to messenger but results in error and shuts down xatkit. needs to be fixed
            //reply(id.getAsString());
        });
    }

    // For testing purposes
    // TODO: remove when better implementation is ready
    private void reply(String senderId) {
        // TODO: replace with logging
        System.out.println("Replying to: " + senderId);

        val body = new JsonObject();

        val recipent = new JsonObject();
        recipent.add("id", new JsonPrimitive(senderId));
        body.add("recipient", recipent);

        val message = new JsonObject();
        message.add("text", new JsonPrimitive("This is a test."));
        body.add("message", message);

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);


        val response = postJsonRequestWithBody(
                ExecutionFactoryImpl.init().createStateContext(),
                "https://graph.facebook.com/v8.0/me/messages",
                new HashMap<>(),
                new HashMap<>(),
                body,
                headers);

        // TODO: replace with logging
        System.out.println("STATUS: " + response.getStatus() + " " + response.getStatusText());
        System.out.println(response.getBody());
    }

    @Override
    public RestPlatformConfiguration getRestPlatformConfiguration() {
        return restPlatformConfiguration;
    }
}
