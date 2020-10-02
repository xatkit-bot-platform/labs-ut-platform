package com.xatkit.plugins.messenger.platform.io;

import com.google.gson.JsonElement;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.platform.io.WebhookEventProvider;
import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.server.RestHandlerException;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.server.MessengerRestHandler;
import com.xatkit.plugins.messenger.platform.MessengerUtils;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import lombok.val;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.xatkit.plugins.messenger.platform.MessengerUtils.calculateRFC2104HMAC;
import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * A Messenger-based {@link WebhookEventProvider}.
 *
 * @see MessengerPlatform
 * @see MessengerRestHandler
 */
public class MessengerIntentProvider extends WebhookEventProvider<MessengerPlatform, MessengerRestHandler> {
    /**
     * Constructs a {@link MessengerIntentProvider} and binds it to the provided {@code platform}.
     *
     * @param platform the {@link MessengerPlatform} managing this provider
     */
    public MessengerIntentProvider(final @NonNull MessengerPlatform platform) {
        super(platform);
    }

    /**
     * Returns the URI of the REST endpoint to register the provider to.
     */
    @Override
    public String getEndpointURI() {
        return MessengerUtils.WEBHOOK_URI;
    }

    /**
     * Creates a {@link MessengerRestHandler} used to process incoming Rest requests.
     */
    @Override
    protected MessengerRestHandler createRestHandler() {
        return new MessengerRestHandler() {
            @Override
            public HttpEntity handleParsedContent(
                    final @Nonnull List<Header> headers,
                    final @Nonnull List<NameValuePair> params,
                    final @Nullable MessengerContent content
            ) throws RestHandlerException {
                Log.debug("Received content.");
                try {
                    checkArgument(nonNull(content), "Missing content.");
                    verifyValidation(headers, content.getRawContent());

                    requireNonNull(content.getJsonElement().getAsJsonObject().get("entry"), "Missing entry.")
                            .getAsJsonArray()
                            .forEach(entry -> handleEntry(entry));

                    return new StringEntity("RECEIVED", StandardCharsets.UTF_8);
                } catch (NullPointerException | IllegalStateException | IllegalArgumentException | XatkitException | InvalidKeyException | NoSuchAlgorithmException e) {
                    throw new RestHandlerException(HttpStatus.SC_FORBIDDEN, e.getMessage(), e);
                }
            }
        };
    }

    private void verifyValidation(final List<Header> headers, final String content) throws RestHandlerException, InvalidKeyException, NoSuchAlgorithmException {
        for (Header header : headers) {
            if (header.getName().equals("X-Hub-Signature") && header.getElements()[0].getName().equals("sha1")) {
                if (calculateRFC2104HMAC(content, runtimePlatform.getAppSecret()).equals(header.getElements()[0].getValue())) {
                    return;
                }
                throw new RestHandlerException(HttpStatus.SC_FORBIDDEN, "Incoming JSON has incorrect validation code");
            }
        }
        throw new RestHandlerException(HttpStatus.SC_FORBIDDEN, "Incoming JSON has no validation code");
    }

    private void handleEntry(final JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            return;
        }
        val jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.has("messaging")) {
            return;
        }
        jsonObject.get("messaging")
                .getAsJsonArray()
                .forEach(this::handleMessaging);
    }

    private void handleMessaging(JsonElement messaging) {
        val messagingJsonObject = messaging.getAsJsonObject();

        val sender = requireNonNull(messagingJsonObject.get("sender"), "Message has no sender");
        val id = requireNonNull(sender.getAsJsonObject().get("id"), "Sender has no id").getAsString();

        val context = this.xatkitBot.getOrCreateContext(id);
        this.getRuntimePlatform().replyWith200(context);

        if (messagingJsonObject.has("message")) {
            val message = messagingJsonObject.get("message");
            handleMessage(message,context);
        }

        if (messagingJsonObject.has("reaction")) {
            val reaction = messagingJsonObject.get("reaction");
            handleReaction(reaction,context);
        }
    }

    private void handleMessage(JsonElement message, StateContext context) {
        val messageJsonObject = message.getAsJsonObject();

        if (!messageJsonObject.has("text")) {
            Log.error("Didn't recognize message because it had no text!");
            return;
        }

        val text = messageJsonObject.get("text").getAsString();

        produceIntentFromRawText(text,context);
    }

    private void handleReaction(JsonElement reactionElement, StateContext context) {
        val reactionJsonObject = reactionElement.getAsJsonObject();

        //For some bizarre reason, it doesn't actually tell us what reaction was removed, only the message ID of the message it was removed from.
        val action = requireNonNull(reactionJsonObject.get("action"), "There is no action").toString();
        String emoji = null;
        if (reactionJsonObject.has("emoji")) { emoji = reactionJsonObject.get("emoji").toString(); }
        String reaction = null;
        if (reactionJsonObject.has("reaction")) { reaction = reactionJsonObject.get("reaction").toString(); }

        String reactionInterpertation = null;
        if (reaction != null) reactionInterpertation = reaction;

        if (reactionInterpertation != null) produceIntentFromRawText(reactionInterpertation,context);
    }

    private void produceIntentFromRawText(String text, StateContext context) {
        Log.debug("Recognizing intention from text \"{0}\"",text);
        try {
            val recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(text,
                    context, this.getRuntimePlatform().getXatkitBot());
            recognizedIntent.getPlatformData().put("rawMessage", text);
            this.sendEventInstance(recognizedIntent, context);
        } catch (IntentRecognitionProviderException e) {
            throw new XatkitException("An internal error occurred when computing the intent.", e);
        }
    }
}
