package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.JsonElement;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xatkit.core.platform.action.RuntimeActionResult;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.entity.Messaging;
import com.xatkit.plugins.messenger.platform.entity.SenderAction;
import com.xatkit.plugins.rest.platform.utils.ApiResponse;
import fr.inria.atlanmod.commons.log.Log;

import java.io.IOException;

import static com.xatkit.core.platform.action.RuntimeArtifactAction.DEFAULT_MESSAGE_DELAY;
import static com.xatkit.core.platform.action.RuntimeArtifactAction.MESSAGE_DELAY_KEY;
import static java.util.Objects.nonNull;

/**
 * MessageReply action based on {@link Reply}.
 * Delay is applied before sending the reply (if so configured).
 * If sending resulted in an IOException (typically caused by a connection issue)
 * the message will be sent again up to 3 times.
 *
 * @see Reply
 */
public class MessageReply extends Reply {
    private final int messageDelay;
    private final Messaging messaging;

    private static final int IO_ERROR_RETRIES = 3;
    private static final int RETRY_WAIT_TIME = 500;

    /**
     * Constructs a Reply
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


    /**
     * Based on {@link com.xatkit.core.platform.action.RuntimeArtifactAction}.
     * This class should be refactored so that it extends RuntimeArtifactAction.
     * This call method is a quick and dirty solution.
     *
     * @see com.xatkit.core.platform.action.RuntimeArtifactAction
     */
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
                if (isDelay(this.messageDelay)) {
                    beforeDelay();
                    waitMessageDelay();
                    afterDelay();
                }

                computationResult = computeAndUnwrapUnirestExceptions();

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

    /**
     * Waits the time specified in the configuration.
     * Waits only if the message contains text.
     */
    private void waitMessageDelay() {
        try {
            Thread.sleep(messageDelay);
        } catch (InterruptedException e) {
            Log.error("An error occurred when waiting for the message delay, see attached exception", e);
        }
    }

    /**
     * Excecutes the this.compute() and returns the results.
     * If {@link UnirestException} is thrown, then the nested exception is unwrapped
     * so that it can be caught in this.call()
     *
     * @return this.compute() result
     */
    protected final ApiResponse<JsonElement> computeAndUnwrapUnirestExceptions() throws Exception {
        try {
            return this.compute();
        } catch (UnirestException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            throw e;
        }
    }

    /**
     * Checks if delay should be applied.
     * Only applies delay if delay time is greater than zero
     * and message contains text (otherwise typing animation will not make sense)
     *
     * @param messageDelay int - the length of the delay (in milliseconds)
     * @return true if delay should be applied else false
     */
    protected boolean isDelay(int messageDelay) {
        return messageDelay > 0 && messaging.getMessage() != null && messaging.getMessage().getText() != null;
    }

    /**
     * Typying animation on before delay
     */
    protected void beforeDelay() {
        ((MessengerPlatform) runtimePlatform).sendAction(context, SenderAction.typingOn);
    }

    /**
     * Typing animation off after delay
     */
    protected void afterDelay() {
        ((MessengerPlatform) runtimePlatform).sendAction(context, SenderAction.typingOff);
    }
}
