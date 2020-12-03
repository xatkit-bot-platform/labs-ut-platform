package com.xatkit.plugins.messenger.platform.io;

import com.google.gson.JsonElement;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.platform.io.WebhookEventProvider;
import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.server.RestHandlerException;
import com.xatkit.dsl.DSL;
import com.xatkit.execution.StateContext;
import com.xatkit.intent.EventDefinition;
import com.xatkit.intent.EventInstance;
import com.xatkit.intent.IntentFactory;
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
import java.util.ArrayList;
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
    public static EventDefinition MessageDelivered = DSL.event("Message_Delivered").getEventDefinition();
    public static EventDefinition MessageRead = DSL.event("Message_Read").getEventDefinition();
    public static EventDefinition MessageUnreact = DSL.event("Message_Unreact").getEventDefinition();
    public static EventDefinition MessageReact = DSL.event("Message_React").getEventDefinition();
    public static EventDefinition MessagePostback = DSL.event("Message_Postback").getEventDefinition();


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

    /**
     * Verifies if the request sent to the endpoint contains a valid signature.
     *
     * @param headers the headers sent with the request
     * @param content the raw content of the request
     * @throws RestHandlerException if the request is invalid
     */
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

    /**
     * Handles the json entry containing messaging
     *
     * @param entry - JsonElement of the entry
     */
    private void handleEntry(final JsonElement entry) {
        if (!entry.isJsonObject()) {
            return;
        }
        val entryObject = entry.getAsJsonObject();
        if (!entryObject.has("messaging")) {
            return;
        }
        entryObject.get("messaging")
                .getAsJsonArray()
                .forEach(this::handleMessaging);
    }

    /**
     * Handles the messaging json element
     *
     * @param messaging JsonElement of the entry
     */
    private void handleMessaging(JsonElement messaging) {
        val messagingJsonObject = messaging.getAsJsonObject();

        val sender = requireNonNull(messagingJsonObject.get("sender"), "Message has no sender");
        val id = requireNonNull(sender.getAsJsonObject().get("id"), "Sender has no id").getAsString();

        val context = this.xatkitBot.getOrCreateContext(id);

        if (checkConfig(MessengerUtils.AUTO_MARK_SEEN_KEY, false)) {
            try {
                this.getRuntimePlatform().markSeen(context);
            } catch (XatkitException e) {
                Log.error(e, "Automatic mark as seen threw an exception (since it is an experimental feature, this might be expected)");
            }
        }

        if (checkConfig(MessengerUtils.HANDLE_DELIVERIES_KEY, false) && messagingJsonObject.has("delivery")) {
            handleDelivery(messagingJsonObject.get("delivery"), context);
        }

        if (checkConfig(MessengerUtils.HANDLE_READ_KEY, false) && messagingJsonObject.has("read")) {
            handleRead(messagingJsonObject.get("read"), context);
        }

        if (messagingJsonObject.has("message")) {
            handleMessage(messagingJsonObject.get("message"), context);
        }

        if (messagingJsonObject.has("postback")) {
            handlePostback(messagingJsonObject.get("postback"), context);
        }

        if (checkConfig(MessengerUtils.HANDLE_REACTIONS_KEY, false) && messagingJsonObject.has("reaction")) {
            handleReaction(messagingJsonObject.get("reaction"), context);
        }
    }

    /**
     * Handles the postback json element
     *
     * @param postback postback JsonElement
     * @param context  messaging context with id equal to sender id
     */
    private void handlePostback(JsonElement postback, StateContext context) {
        val postbackObject = postback.getAsJsonObject();
        EventInstance eventInstance;
        if (checkConfig(MessengerUtils.INTENT_FROM_POSTBACK, false)) {
            String text = null;
            if (postbackObject.has("title") && checkConfig(MessengerUtils.USE_TITLE_TEXT, false)) {
                text = postbackObject.get("title").getAsString();
            } else if (postbackObject.has("emoji")) {
                text = postbackObject.get("emoji").getAsString();
            }
            if (postbackObject.has("payload")) {
                text = postbackObject.get("payload").getAsString();
            }
            eventInstance = produceIntentFromRawText(text, context);
        } else {
            eventInstance = IntentFactory.eINSTANCE.createEventInstance();
            eventInstance.setDefinition(MessagePostback);
        }

        val title = postbackObject.get("title").getAsString();
        eventInstance.getPlatformData().put(MessengerUtils.POSTBACK_TITLE_KEY, title);

        if (postbackObject.has("payload")) {
            val payload = postbackObject.get("payload").getAsString();
            eventInstance.getPlatformData().put(MessengerUtils.POSTBACK_PAYLOAD_KEY, payload);
        }

        if (postbackObject.has("referral")) {
            val referral = postbackObject.get("referral").getAsJsonObject();
            val ref = referral.get("ref").getAsString();
            eventInstance.getPlatformData().put(MessengerUtils.POSTBACK_REFERRAL_REF_KEY, ref);
            val source = referral.get("source").getAsString();
            eventInstance.getPlatformData().put(MessengerUtils.POSTBACK_REFERRAL_SOURCE_KEY, source);
            val type = referral.get("type").getAsString();
            eventInstance.getPlatformData().put(MessengerUtils.POSTBACK_REFERRAL_TYPE_KEY, type);
        }

        sendEventInstance(eventInstance, context);
    }

    /**
     * Handles the delivery json element
     *
     * @param delivery delivery JsonElement
     * @param context  messaging context with id equal to sender id
     */
    private void handleDelivery(JsonElement delivery, StateContext context) {
        val eventInstance = IntentFactory.eINSTANCE.createEventInstance();
        eventInstance.setDefinition(MessageDelivered);
        val deliveryObject = delivery.getAsJsonObject();

        val mids = new ArrayList<String>();
        deliveryObject.get("mids").getAsJsonArray().forEach(mid -> mids.add(mid.getAsString()));
        eventInstance.getPlatformData().put(MessengerUtils.MESSAGE_IDS_KEY, mids);

        val watermark = deliveryObject.get("watermark").getAsLong();
        eventInstance.getPlatformData().put(MessengerUtils.WATERMARK_KEY, watermark);
        sendEventInstance(eventInstance, context);
    }

    /**
     * Handles the read json element
     *
     * @param read    read JsonElement
     * @param context messaging context with id equal to sender id
     */
    private void handleRead(JsonElement read, StateContext context) {
        val eventInstance = IntentFactory.eINSTANCE.createEventInstance();
        eventInstance.setDefinition(MessageRead);
        val watermark = read.getAsJsonObject().get("watermark").getAsLong();
        eventInstance.getPlatformData().put(MessengerUtils.WATERMARK_KEY, watermark);
        sendEventInstance(eventInstance, context);
    }

    /**
     * Handles the message json element
     *
     * @param message message JsonElement
     * @param context messaging context with id equal to sender id
     */
    private void handleMessage(JsonElement message, StateContext context) {
        val messageJsonObject = message.getAsJsonObject();

        if (!messageJsonObject.has("text")) {
            Log.error("Didn't recognize message because it had no text!");
            return;
        }

        val text = messageJsonObject.get("text").getAsString();

        val eventInstance = produceIntentFromRawText(text, context);
        sendEventInstance(eventInstance, context);
    }

    /**
     * Handles the reaction json element
     *
     * @param reactionElement postback JsonElement
     * @param context         messaging context with id equal to sender id
     */
    private void handleReaction(JsonElement reactionElement, StateContext context) {
        val reactionJsonObject = reactionElement.getAsJsonObject();
        EventInstance eventInstance;
        if (checkConfig(MessengerUtils.INTENT_FROM_REACTION, false)) {
            String text = null;
            if (reactionJsonObject.has("reaction") && checkConfig(MessengerUtils.USE_REACTION_TEXT, false)) {
                text = reactionJsonObject.get("reaction").getAsString();
            } else if (reactionJsonObject.has("emoji")) {
                text = reactionJsonObject.get("emoji").getAsString();
            }
            eventInstance = produceIntentFromRawText(text, context);
        } else {
            eventInstance = IntentFactory.eINSTANCE.createEventInstance();
        }

        val action = requireNonNull(reactionJsonObject.get("action"), "There is no action").getAsString();
        val mid = requireNonNull(reactionJsonObject.get("mid"), "There is no message id").getAsString();
        eventInstance.getPlatformData().put(MessengerUtils.MESSAGE_ID_KEY, mid);

        if (action.equals("react")) {
            if (!checkConfig(MessengerUtils.INTENT_FROM_REACTION, false)) eventInstance.setDefinition(MessageReact);

            if (reactionJsonObject.has("emoji")) {
                val emoji = reactionJsonObject.get("emoji").getAsString();
                eventInstance.getPlatformData().put(MessengerUtils.EMOJI_KEY, emoji);
            }

            if (reactionJsonObject.has("reaction")) {
                val reaction = reactionJsonObject.get("reaction").getAsString();
                eventInstance.getPlatformData().put(MessengerUtils.REACTION_KEY, reaction);
            }
        } else if (action.equals("unreact")) {
            if (!checkConfig(MessengerUtils.INTENT_FROM_REACTION, false)) eventInstance.setDefinition(MessageUnreact);
        } else {
            throw new XatkitException("Unrecognised action");
        }

        Log.debug("Recognized {0} event", action);
        sendEventInstance(eventInstance, context);
    }

    /**
     * Creates intent from raw text.
     *
     * @param text    text to create the intent from
     * @param context messaging context with id equal to sender id
     * @return intent recognized from the raw text
     */
    private EventInstance produceIntentFromRawText(String text, StateContext context) {
        Log.debug("Recognizing intention from text \"{0}\"", text);
        try {
            val recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(
                    text, context, this.getRuntimePlatform().getXatkitBot());
            recognizedIntent.getPlatformData().put(MessengerUtils.RAW_TEXT_KEY, text);
            return recognizedIntent;
        } catch (IntentRecognitionProviderException e) {
            throw new XatkitException("An internal error occurred when computing the intent.", e);
        }
    }

    /**
     * Checks the runtime platform for configuration with a given key and returns it's value (boolean)
     * If such configuration node does not exist, returs the default value given.
     * Nodes checked must have a boolean value.
     *
     * @param configuration configuration node key to check
     * @param miss          default value to return when key is not present
     * @return boolean configuration value of the given key or miss parameter value if key not found.
     */
    private boolean checkConfig(String configuration, boolean miss) {
        return runtimePlatform.getConfiguration().getBoolean(configuration, miss);
    }
}
