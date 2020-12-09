package com.xatkit.plugins.messenger.platform;

import com.google.gson.JsonElement;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.core.server.*;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.action.*;
import com.xatkit.plugins.messenger.platform.entity.*;
import com.xatkit.plugins.messenger.platform.entity.payloads.AttachmentIdPayload;
import com.xatkit.plugins.messenger.platform.entity.response.MessengerException;
import com.xatkit.plugins.messenger.platform.entity.response.MessengerResponse;
import com.xatkit.plugins.rest.platform.RestPlatform;
import com.xatkit.plugins.rest.platform.action.JsonRestRequest;
import com.xatkit.plugins.rest.platform.utils.ApiResponse;
import lombok.Getter;
import lombok.NonNull;
import fr.inria.atlanmod.commons.log.Log;
import lombok.val;
import lombok.var;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import static java.util.Objects.requireNonNull;

/**
 * A {@link RuntimePlatform} class that connects and interacts with the Messenger API.
 */
public class MessengerPlatform extends RestPlatform {
    private String verifyToken;
    @Getter
    private String accessToken;
    @Getter
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
                        throw new RestHandlerException(HttpStatus.SC_FORBIDDEN, "Received token does not match the verify token.");
                    }
                    return new StringEntity(challenge, StandardCharsets.UTF_8);
                }));
    }

    /**
     * Mark messages as seen
     *
     * @param context the current {@link StateContext}
     * @return the {@link MessengerResponse}
     */
    public MessengerResponse markSeen(@NonNull StateContext context) {
        return sendAction(context, SenderAction.markSeen);
    }

    /**
     * Send an action. Actions: "mark_seen", "typing_on" or "typing_off".
     * Sender actions "typing_on" and "typing_off" won't be supported by Facebook for European users
     * since 16th of December 2020.
     *
     * @param context      the current {@link StateContext}
     * @param senderAction the {@link SenderAction}
     * @return the {@link MessengerResponse}
     */
    public MessengerResponse sendAction(@NonNull StateContext context, @NonNull SenderAction senderAction) {
        val recipientId = MessengerUtils.extractContextId(context.getContextId());
        Log.debug("Replying to {0} with a sender_action {1}", recipientId, senderAction.name());
        val messaging = new Messaging(new Recipient(recipientId), senderAction);
        return reply(new Reply(this, context, messaging));
    }

    /**
     * Upload a file. This won't actually send the file to the user.
     * Instead response will have an id of the attachment that can be sent to the user with {@link #sendFile(StateContext, File)}  sendFile} method.
     * Curretly, the attachment_id is also added to the {@link File}.
     * <p>
     * If a request is successfully sent returns a {@link MessengerResponse}.
     * If an error response is received from Facebook throws a {@link MessengerException}
     * If an unknown error is encountered throws a {@link XatkitException}
     * <p>
     * Media/attachment (audio, video, files) other than images
     * won't be supported by Facebook for European users since 16th of December 2020.
     *
     * @param context the current {@link StateContext}
     * @param file    the {@link File}
     * @return the {@link MessengerResponse}
     */
    public MessengerResponse uploadFile(@NonNull StateContext context, File file) {
        return excecuteRequest(new FileReply(this, context, file));
    }

    /**
     * Send attachment with a given id. You can get the id after uploading the file with {@link #uploadFile(StateContext, File) uploadFile} method
     * <p>
     * If a request is successfully sent returns a {@link MessengerResponse}.
     * If an error response is received from Facebook throws a {@link MessengerException}
     * If an unknown error is encountered throws a {@link XatkitException}
     * <p>
     * Media/attachment (audio, video, files) other than images
     * won't be supported by Facebook for European users since 16th of December 2020.
     *
     * @param context        the current {@link StateContext}
     * @param attachmentId   id of the attachment to be sent.
     * @param attachmentType the {@link com.xatkit.plugins.messenger.platform.entity.Attachment.AttachmentType AttachmentType}
     * @return the {@link MessengerResponse}
     */
    public MessengerResponse sendFile(@NonNull StateContext context, @NonNull String attachmentId, @NonNull Attachment.AttachmentType attachmentType) {
        val recipientId = MessengerUtils.extractContextId(context.getContextId());
        val messaging = new Messaging(
                new Recipient(recipientId),
                new Message(new Attachment(attachmentType, new AttachmentIdPayload(attachmentId))));
        Log.debug("SENDING FILE TO: {0}", recipientId);
        return reply(new MessageReply(this, context, messaging));
    }

    /**
     * Sends {@link File}. If the {@link File} doesn't have an attachment_id, then uploads the file and adds an attachment_id to it.
     * Otherwise assumes that the attachment_id of the file is correct and uses it to send the file.
     * Calls {@link #sendFile(StateContext, File, boolean)}
     * <p>
     * Media/attachment (audio, video, files) other than images
     * won't be supported by Facebook for European users since 16th of December 2020.
     *
     * @param context the current {@link StateContext}
     * @param file    the {@link File}
     * @return the {@link MessengerResponse}
     * @see #sendFile(StateContext, File, boolean)
     * @see #sendFile(StateContext, String, Attachment.AttachmentType)
     */
    public MessengerResponse sendFile(@NonNull StateContext context, @NonNull File file) {
        return sendFile(context, file, false);
    }

    /**
     * Sends the {@link File}. If the {@link File} doesn't have an attachment_id, then uploads the file and adds an attachment_id to it.
     * If reupload is true, always uploads the file, otherwise assumes that the attachment_id present in the {@link File} is correct.
     * Calls {@link #sendFile(StateContext, String, Attachment.AttachmentType)}.
     * <p>
     * Media/attachment (audio, video, files) other than images
     * won't be supported by Facebook for European users since 16th of December 2020.
     *
     * @param context  the current {@link StateContext}
     * @param file     the {@link File}
     * @param reupload whether to reupload the file or not.
     * @return the {@link MessengerResponse}
     * @see #sendFile(StateContext, String, Attachment.AttachmentType)
     */
    public MessengerResponse sendFile(@NonNull StateContext context, @NonNull File file, boolean reupload) {
        var attachmentId = file.getAttachmentId();
        if (StringUtils.isEmpty(attachmentId) || reupload) {
            val response = uploadFile(context, file);
            attachmentId = response.getAttachmentId();
        }

        return sendFile(context, attachmentId, file.getAttachment().getType());
    }

    /**
     * Sends a message with given text with the option to turn on naturalization.
     * Wraps the text in {@link Message} and calls {@link #reply(StateContext, Message)}
     *
     * @param context    the current {@link StateContext}
     * @param text       text to send as a message.
     * @param naturalize used to turn on text naturalization.
     * @return the {@link MessengerResponse}
     * @see #reply(StateContext, Message)
     */
    public MessengerResponse reply(@NonNull StateContext context, @NonNull String text, boolean naturalize) {
        if (naturalize && getConfiguration().getBoolean(MessengerUtils.NATURALIZE_TEXT, false)) {
            text = TextNaturalizer.get().naturalize(text);
        }
        return reply(context, text);
    }

    /**
     * Sends a message with given text.
     * Wraps the text in {@link Message} and calls {@link #reply(StateContext, Message)}
     *
     * @param context the current {@link StateContext}
     * @param text    text to send as a message.
     * @return the {@link MessengerResponse}
     * @see #reply(StateContext, Message)
     */
    public MessengerResponse reply(@NonNull StateContext context, @NonNull String text) {
        return reply(context, new Message(text));
    }

    /**
     * Sends a message with given attachment.
     * Wraps the attachment in {@link Message} and calls {@link #reply(StateContext, Message)}
     *
     * @param context    the current {@link StateContext}
     * @param attachment attachment to send as a message.
     * @return the {@link MessengerResponse}
     * @see #reply(StateContext, Message)
     */
    public MessengerResponse reply(@NonNull StateContext context, @NonNull Attachment attachment) {
        return reply(context, new Message(attachment));
    }

    /**
     * Sends a {@link Message}.
     * If a request is successfully sent returns a {@link MessengerResponse}.
     * If an error response is received from Facebook throws a {@link MessengerException}
     * If an unknown error is encountered throws a {@link XatkitException}
     *
     * @param context the current {@link StateContext}
     * @param message {@link Message} to be sent.
     * @return the {@link MessengerResponse}
     */
    public MessengerResponse reply(@NonNull StateContext context, @NonNull Message message) {
        val recipientId = MessengerUtils.extractContextId(context.getContextId());
        Log.debug("REPLYING TO: {0}", recipientId);
        val messaging = new Messaging(new Recipient(recipientId), message);
        return reply(new MessageReply(this, context, messaging));
    }

    /**
     * Sends a {@link Reply}.
     *
     * @param reply {@link Reply} to be sent.
     * @return the {@link MessengerResponse}
     */
    private MessengerResponse reply(@NonNull Reply reply) {
        return excecuteRequest(reply);
    }

    /**
     * Excecutes a request.
     * <p>
     * If a request is successfully sent returns a {@link MessengerResponse}.
     * If an error response is received from Facebook throws a {@link MessengerException}
     * If an unknown error is encountered throws a {@link XatkitException}
     *
     * @param request request to be sent.
     * @return the {@link MessengerResponse}
     */
    private MessengerResponse excecuteRequest(JsonRestRequest<JsonElement> request) {
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
                    throw new MessengerException(status, code, subcode, fbtraceId, message);
                }
                throw new XatkitException(apiResponse.getStatusText());
            }
            Log.debug("REPLY RESPONSE STATUS: {0} {1}\n BODY: {2}", apiResponse.getStatus(), apiResponse.getStatusText(), apiResponse.getBody().toString());
            val recipientId = responseBody.has("recipient_id") ? responseBody.get("recipient_id").getAsString() : null;
            val attachmentId = responseBody.has("attachment_id") ? responseBody.get("attachment_id").getAsString() : null;
            val messageId = responseBody.has("message_id") ? responseBody.get("message_id").getAsString() : null;
            return new MessengerResponse(status, recipientId, messageId, attachmentId);
        }
        val error_message = MessageFormat.format("Unexpected reply result: {0}", result);
        Log.error(error_message);
        throw new XatkitException(error_message);
    }
}
