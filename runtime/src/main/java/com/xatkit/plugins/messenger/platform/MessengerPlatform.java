package com.xatkit.plugins.messenger.platform;

import com.google.gson.JsonElement;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.core.server.*;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.action.*;
import com.xatkit.plugins.messenger.platform.entity.*;
import com.xatkit.plugins.messenger.platform.entity.payloads.AttachmentIdPayload;
import com.xatkit.plugins.rest.platform.RestPlatform;
import com.xatkit.plugins.rest.platform.action.JsonRestRequest;
import com.xatkit.plugins.rest.platform.utils.ApiResponse;
import lombok.NonNull;
import fr.inria.atlanmod.commons.log.Log;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;

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

        xatkitBot.getXatkitServer().registerRestEndpoint(HttpMethod.GET, MessengerUtils.WEBHOOK_URI,
                RestHandlerFactory.createEmptyContentRestHandler((headers, params, content) -> {
                    val mode = requireNonNull(HttpUtils.getParameterValue("hub.mode", params), "Missing mode");
                    val token = requireNonNull(HttpUtils.getParameterValue("hub.verify_token", params), "Missing token");
                    val challenge = requireNonNull(HttpUtils.getParameterValue("hub.challenge", params), "Missing challenge");
                    if (!mode.equals("subscribe")) {
                        throw new RestHandlerException(HttpStatus.SC_FORBIDDEN, "Mode is not 'subscribe'");
                    }
                    if (!token.equals(verifyToken)) {
                        throw new RestHandlerException(HttpStatus.SC_FORBIDDEN, "Token does not match verify token.");
                    }
                    return new StringEntity(challenge, StandardCharsets.UTF_8);
                }));
    }

    public SendResponse markSeen(@NonNull StateContext context) {
        return sendAction(context, SenderAction.markSeen);
    }

    public SendResponse sendAction(@NonNull StateContext context, @NonNull SenderAction senderAction) {
        val recipientId = context.getContextId();
        Log.debug("Replying to {0} with a sender_action {1}", recipientId, senderAction.name());
        val messaging = new Messaging(new Recipient(recipientId), senderAction);
        return reply(new Reply(this, context, messaging));
    }

    public UploadResponse uploadFile(@NonNull StateContext context, File file) {
        val apiResponse = excecuteRequest(new FilePost(this, context, file));
        if (apiResponse != null) {
            val responseBody = apiResponse.getBody().getAsJsonObject();
            val attachmentId = responseBody.get("attachment_id").getAsString();
            return new UploadResponse(apiResponse.getStatus(), attachmentId);
        }
        return null;
    }

    public SendResponse sendFile(@NonNull StateContext context, File file) {
        val response = uploadFile(context, file);
        if (response == null) {
            Log.error("Could not upload the file.");
            return null;
        }
        val attachmentId = response.getAttachmentId();
        val attachmentType = file.getAttachment().getType();
        val recipientId = context.getContextId();
        Log.debug("SENDING FILE TO: {0}", recipientId);
        return reply(new Reply(
                this,
                context,
                new Messaging(
                        new Recipient(recipientId),
                        new Message(new Attachment(attachmentType,
                                new AttachmentIdPayload(attachmentId))))
                )
        );
    }

    public SendResponse reply(@NonNull StateContext context, @NonNull String text) {
        return reply(context, new Message(text));
    }

    public SendResponse reply(@NonNull StateContext context, @NonNull Message message) {
        val recipientId = context.getContextId();
        Log.debug("REPLYING TO: {0}", recipientId);
        val messaging = new Messaging(new Recipient(recipientId), message);
        return reply(new MessageReply(this, context, messaging));
    }

    private SendResponse reply(@NonNull Reply reply) {
        val apiResponse = excecuteRequest(reply);
        if (apiResponse != null) {
            val responseBody = apiResponse.getBody().getAsJsonObject();
            System.out.println(responseBody.toString());
            val recipientId = responseBody.get("recipient_id").getAsString();
            val messageId = responseBody.has("message_id")? responseBody.get("message_id").getAsString() : null;
            return new SendResponse(apiResponse.getStatus(), recipientId, messageId);
        }
        return null;
    }

    private ApiResponse<JsonElement> excecuteRequest(JsonRestRequest<JsonElement> request) {
        val result = request.call().getResult();

        if (result instanceof ApiResponse) {
            val apiResponse = (ApiResponse<JsonElement>) result;
            if (apiResponse.getStatus() < 200 || apiResponse.getStatus() > 299) {
                Log.error("REPLY RESPONSE STATUS: {0} {1}\n BODY: {2}", apiResponse.getStatus(), apiResponse.getStatusText(), apiResponse.getBody().toString());
                return null;
            }
            Log.debug("REPLY RESPONSE STATUS: {0} {1}\n BODY: {2}", apiResponse.getStatus(), apiResponse.getStatusText(), apiResponse.getBody().toString());
            return apiResponse;
        }
        Log.error("Unexpected reply result: {0}", result);
        return null;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
