package com.xatkit.plugins.messenger.platform.action;

import com.xatkit.core.platform.action.RuntimeActionResult;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.data.Messaging;
import com.xatkit.plugins.messenger.platform.data.SenderAction;
import fr.inria.atlanmod.commons.log.Log;

import static com.xatkit.core.platform.action.RuntimeArtifactAction.DEFAULT_MESSAGE_DELAY;
import static com.xatkit.core.platform.action.RuntimeArtifactAction.MESSAGE_DELAY_KEY;

public class MessageReply extends Reply {
    private final int messageDelay;
    private final Messaging messaging;

    /**
     * Constructs a POST Json request with a body parameter.
     * Delay is applied before sending the reply.
     *
     * @param platform  the {@link MessengerPlatform} containing this action
     * @param context   the {@link StateContext} associated to this action
     * @param messaging the body of the request
     */
    public MessageReply(MessengerPlatform platform, StateContext context, Messaging messaging) {
        super(platform, context, messaging);
        this.messageDelay = this.runtimePlatform.getConfiguration().getInt(MESSAGE_DELAY_KEY, DEFAULT_MESSAGE_DELAY);
        this.messaging = messaging;
    }

    @Override
    public RuntimeActionResult call() {
        if (messageDelay > 0) {
            beforeDelay();
            waitMessageDelay();
            afterDelay();
        }
        return super.call();
    }

    private void waitMessageDelay() {
        if (this.messageDelay > 0 && messaging.getMessage() != null && messaging.getMessage().getText() != null) {
            try {
                Thread.sleep(messageDelay);
            } catch (InterruptedException e) {
                Log.error("An error occurred when waiting for the message delay, see attached exception", e);
            }
        }
    }

    protected void beforeDelay() {
        ((MessengerPlatform) runtimePlatform).sendAction(context, SenderAction.typingOn);
    }

    protected void afterDelay() {
        ((MessengerPlatform) runtimePlatform).sendAction(context, SenderAction.typingOff);
    }
}
