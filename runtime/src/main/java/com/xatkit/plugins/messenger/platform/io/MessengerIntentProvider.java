package com.xatkit.plugins.messenger.platform.io;

import com.google.gson.JsonElement;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.platform.io.WebhookEventProvider;
import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.server.RestHandlerException;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.MessengerRestHandler;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class MessengerIntentProvider extends WebhookEventProvider<MessengerPlatform, MessengerRestHandler> {
    MessengerRestHandler restHandler;

    public MessengerIntentProvider(@NonNull MessengerPlatform platform) {
        super(platform);
    }

    public void start(@NonNull Configuration configuration) {
            this.restHandler = createRestHandler();
            super.start(configuration);
    }

    @Override
    public MessengerRestHandler getRestHandler() {
        return restHandler;
    }

    @Override
    public String getEndpointURI() {
        return "/messenger/webhook";
    }

    @Override
    protected MessengerRestHandler createRestHandler() {
        return new MessengerRestHandler() {
            // TODO: add validation of post request https://developers.facebook.com/docs/messenger-platform/webhook#security
            @Override
            public HttpEntity handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params, @Nullable JsonElement content) throws RestHandlerException {
                try {
                    checkArgument(nonNull(content), "Missing content.");

                    requireNonNull(content.getAsJsonObject().get("entry"), "Missing entry.")
                            .getAsJsonArray()
                            .iterator()
                            .forEachRemaining(entry -> handleWebhookEntry(entry));

                    return new StringEntity("RECEIVED", "UTF-8");
                } catch (NullPointerException | IllegalStateException | IllegalArgumentException e) {
                    throw new RestHandlerException(403, e.getMessage());
                }
            }};
    }

    private void handleWebhookEntry(JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            return;
        }
        val jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.has("messaging")) {
            return;
        }
        val messaging = jsonObject.get("messaging").getAsJsonArray();
        messaging.iterator().forEachRemaining(messagingEntity -> {
            val messageJsonObject = messagingEntity.getAsJsonObject();
            val sender = requireNonNull(messageJsonObject.get("sender"), "Message has no sender");
            val id = requireNonNull(sender.getAsJsonObject().get("id"), "Sender has no id").getAsString();

            val message = requireNonNull(messageJsonObject.get("message"), "Message has no content");
            val text = requireNonNull(message.getAsJsonObject().get("text"), "Message has no text").getAsString();

            try {
                StateContext context  = this.xatkitCore.getOrCreateContext(id);
                val recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(text,
                        context, this.getRuntimePlatform().getXatkitCore());
                this.sendEventInstance(recognizedIntent, context);
            } catch (IntentRecognitionProviderException e) {
                throw new XatkitException("An internal error occurred when computing the intent, see attached exception", e);
            }
        });
    }
}
