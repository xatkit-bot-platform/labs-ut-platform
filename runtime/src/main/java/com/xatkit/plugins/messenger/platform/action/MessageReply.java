package com.xatkit.plugins.messenger.platform.action;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.xatkit.core.platform.action.RuntimeActionResult;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.entity.Messaging;
import com.xatkit.plugins.messenger.platform.entity.SenderAction;
import fr.inria.atlanmod.commons.log.Log;

import java.io.IOException;

import static com.xatkit.core.platform.action.RuntimeArtifactAction.DEFAULT_MESSAGE_DELAY;
import static com.xatkit.core.platform.action.RuntimeArtifactAction.MESSAGE_DELAY_KEY;
import static java.util.Objects.nonNull;

public class MessageReply extends Reply {
    private final int messageDelay;
    private final Messaging messaging;

    private static final int IO_ERROR_RETRIES = 3;
    private static final int RETRY_WAIT_TIME = 500;

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


    // this class should be refactored so that it extends RuntimeArtifactAction.
    // this call method is a quick and dirty solution
    @Override
    public RuntimeActionResult call() {
        Object computationResult = null;
        Exception thrownException;
        int attempts = 0;
        long before = System.currentTimeMillis();


        /*
         * We use a do-while here because the thrownException value is initialized with null, and we want to perform
         * at least one iteration of the loop. If the thrownException value is still null after an iteration we can
         * exit the loop: the underlying action computation finished without any exception.
         */
        do {
            /*
             * Reset the thrownException, if we are retrying to send a artifact the previously stored exception is not
             * required anymore: we can forget it and replace it with the potential new exception.
             */
            thrownException = null;
            attempts++;
            if (attempts > 1) {
                /*
                 * If this is not the first attempt we need to wait before sending again the artifact. The waiting
                 * time is equal to (iteration - 1) * RETRY_TIME: the second iteration will wait for RETRY_TIME, the
                 * third one for 2 * RETRY_TIME, etc.
                 */
                int waitTime = (attempts - 1) * RETRY_WAIT_TIME;
                Log.info("Waiting {0} ms before trying to send the artifact again", waitTime);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e1) {
                    /*
                     * Ignore the exception, the Thread has been interrupted but we can still compute the action.
                     */
                    Log.warn("An error occurred while waiting to send the artifact, trying to send it right now", e1);
                }
            }
            try {
                if (messageDelay > 0) {
                    beforeDelay();
                    waitMessageDelay();
                    afterDelay();
                }

                try {
                    computationResult = this.compute();
                } catch (UnirestException e) {
                    throw (Exception) e.getCause();
                }

            } catch (IOException e) {
                if (attempts < IO_ERROR_RETRIES + 1) {
                    Log.error("An {0} occurred when computing the action, trying to send the artifact again ({1}/{2})"
                            , e
                                    .getClass().getSimpleName(), attempts, IO_ERROR_RETRIES);
                } else {
                    Log.error("Could not compute the action: {0}", e.getClass().getSimpleName());
                }
                /*
                 * Set the thrownException value, if the compute() method fails with an IOException every time we
                 * need to return an error message with it.
                 */
                thrownException = e;
            } catch (Exception e) {
                thrownException = e;
                /*
                 * We caught a non-IO exception: an internal error occurred when computing the action. We assume that
                 * internal errors cannot be solved be recomputing the action, so we break and return the
                 * RuntimeActionResult directly.
                 */
                break;
            }
            /*
             * Exit on IO_ERROR_RETRIES + 1: the first one is the standard execution, then we can retry
             * IO_ERROR_RETRIES times.
             */
        } while (nonNull(thrownException) && attempts < IO_ERROR_RETRIES + 1);
        long after = System.currentTimeMillis();
        return new RuntimeActionResult(computationResult, thrownException, (after - before));
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
