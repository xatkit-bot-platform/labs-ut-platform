package com.xatkit.plugins.messenger.platform;

import com.google.gson.JsonElement;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.core.server.*;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.action.*;
import com.xatkit.plugins.messenger.platform.entity.*;
import com.xatkit.plugins.messenger.platform.entity.payloads.AttachmentIdPayload;
import com.xatkit.plugins.messenger.platform.entity.response.ErrorResponse;
import com.xatkit.plugins.messenger.platform.entity.response.Response;
import com.xatkit.plugins.messenger.platform.entity.response.SendResponse;
import com.xatkit.plugins.rest.platform.RestPlatform;
import com.xatkit.plugins.rest.platform.action.JsonRestRequest;
import com.xatkit.plugins.rest.platform.utils.ApiResponse;
import lombok.NonNull;
import fr.inria.atlanmod.commons.log.Log;
import lombok.val;
import lombok.var;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
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

    public Response markSeen(@NonNull StateContext context) {
        return sendAction(context, SenderAction.markSeen);
    }

    public Response sendAction(@NonNull StateContext context, @NonNull SenderAction senderAction) {
        val recipientId = MessengerUtils.extractContextId(context.getContextId());
        Log.debug("Replying to {0} with a sender_action {1}", recipientId, senderAction.name());
        val messaging = new Messaging(new Recipient(recipientId), senderAction);
        return reply(new Reply(this, context, messaging));
    }

    public Response uploadFile(@NonNull StateContext context, File file) {
        return excecuteRequest(new FileReply(this, context, file));
    }

    public Response sendFile(@NonNull StateContext context, @NonNull String attachmentId, @NonNull Attachment.AttachmentType attachmentType) {
        val recipientId = MessengerUtils.extractContextId(context.getContextId());
        val messaging = new Messaging(
                new Recipient(recipientId),
                new Message(new Attachment(attachmentType, new AttachmentIdPayload(attachmentId))));
        Log.debug("SENDING FILE TO: {0}", recipientId);
        return reply(new Reply(this, context, messaging)
        );
    }

    public Response sendFile(@NonNull StateContext context, @NonNull File file) {
        var attachmentId = file.getAttachmentId(); //I did not use the custom extractContextId here, so this is a potential error place
        if (StringUtils.isEmpty(attachmentId)) {
            val response = uploadFile(context, file);
            if (!(response instanceof SendResponse)) {
                Log.error("Could not upload the file.");
                return response;
            }
            attachmentId = ((SendResponse) response).getAttachmentId();
        }

        return sendFile(context, attachmentId, file.getAttachment().getType());
    }

    public Response reply(@NonNull StateContext context, @NonNull String text) {
        if (getConfiguration().getBoolean(MessengerUtils.NATURALIZE_TEXT, false)) text = TextNaturalizer.get().naturalize(text);

        return reply(context, new Message(text));
    }

    public Response reply(@NonNull StateContext context, @NonNull Attachment attachment) {
        return reply(context, new Message(attachment));
    }

    public Response reply(@NonNull StateContext context, @NonNull Message message) {
        val recipientId = MessengerUtils.extractContextId(context.getContextId());
        Log.debug("REPLYING TO: {0}", recipientId);
        val messaging = new Messaging(new Recipient(recipientId), message);
        return reply(new MessageReply(this, context, messaging));
    }

    private Response reply(@NonNull Reply reply) {
        return excecuteRequest(reply);
    }

    private Response excecuteRequest(JsonRestRequest<JsonElement> request) {
        val result = request.call().getResult();

        if (result instanceof ApiResponse) {
            val apiResponse = (ApiResponse<?>) result;
            val responseBody = ((JsonElement) apiResponse.getBody()).getAsJsonObject();
            val status = apiResponse.getStatus();
            if (status < 200 || status > 299) {
                Log.error("REPLY RESPONSE STATUS: {0} {1}\n BODY: {2}", apiResponse.getStatus(), apiResponse.getStatusText(), apiResponse.getBody().toString());
                if (responseBody.has("error")) {
                    val error = responseBody.get("error").getAsJsonObject();
                    val code = error.has("code") ? error.get("code").getAsInt() : null;
                    val subcode = error.has("error_subcode") ? error.get("error_subcode").getAsInt() : null;
                    val fbtraceId = error.has("fbtrace_id") ? error.get("fbtrace_id").getAsString() : null;
                    val message = error.has("message") ? error.get("message").getAsString() : null;
                    return new ErrorResponse(status, code, subcode, fbtraceId, message);
                }
                return null;
            }
            Log.debug("REPLY RESPONSE STATUS: {0} {1}\n BODY: {2}", apiResponse.getStatus(), apiResponse.getStatusText(), apiResponse.getBody().toString());
            val recipientId = responseBody.has("recipient_id") ? responseBody.get("recipient_id").getAsString() : null;
            val attachmentId = responseBody.has("attachment_id") ? responseBody.get("attachment_id").getAsString() : null;
            val messageId = responseBody.has("message_id") ? responseBody.get("message_id").getAsString() : null;
            return new SendResponse(status, recipientId, messageId, attachmentId);
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
