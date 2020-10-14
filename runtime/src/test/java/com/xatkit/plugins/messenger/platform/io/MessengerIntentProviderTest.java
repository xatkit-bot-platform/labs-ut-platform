package com.xatkit.plugins.messenger.platform.io;

import com.xatkit.AbstractEventProviderTest;
import com.xatkit.core.ExecutionService;
import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.server.RestHandlerException;
import com.xatkit.core.server.XatkitServer;
import com.xatkit.execution.ExecutionFactory;
import com.xatkit.execution.StateContext;
import com.xatkit.intent.*;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.MessengerUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static com.xatkit.plugins.messenger.platform.io.MessengerIntentProviderTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.mockito.Mockito.*;

public class MessengerIntentProviderTest extends AbstractEventProviderTest<MessengerIntentProvider, MessengerPlatform> {
    private static IntentDefinition VALID_EVENT_DEFINITION;

    private static RecognizedIntent VALID_RECOGNIZED_INTENT;

    private StateContext context;

    private ExecutionService mockedExecutionService;

    private XatkitServer mockedXatkitServer;

    private Configuration configuration;

    @BeforeClass
    public static void setUpBeforeClass() {
        VALID_EVENT_DEFINITION = IntentFactory.eINSTANCE.createIntentDefinition();
        VALID_EVENT_DEFINITION.setName("Default Welcome Intent");
        VALID_RECOGNIZED_INTENT = IntentFactory.eINSTANCE.createRecognizedIntent();
        VALID_RECOGNIZED_INTENT.setDefinition(VALID_EVENT_DEFINITION);
    }

    @Before
    public void setUp() {
        super.setUp();
        context = ExecutionFactory.eINSTANCE.createStateContext();
        context.setContextId("TEST");
        mockedExecutionService = mock(ExecutionService.class);
        mockedExecutionService = mock(ExecutionService.class);
        when(mockedXatkitBot.getExecutionService()).thenReturn(mockedExecutionService);
        when(mockedXatkitBot.getOrCreateContext(any(String.class))).thenReturn(context);
    }

    @After
    public void tearDown() {
        if (nonNull(provider)) {
            provider.close();
        }
        super.tearDown();
    }

    @SneakyThrows
    @Override
    protected MessengerPlatform getPlatform() {
        mockedXatkitServer = mock(XatkitServer.class);
        when(mockedXatkitBot.getXatkitServer()).thenReturn(mockedXatkitServer);
        when(mockedIntentRecognitionProvider.getIntent(any(String.class), any(StateContext.class))).thenReturn(VALID_RECOGNIZED_INTENT);
        configuration = new BaseConfiguration();
        configuration.addProperty(MessengerUtils.ACCESS_TOKEN_KEY, "TEST_ACCESS");
        configuration.addProperty(MessengerUtils.VERIFY_TOKEN_KEY, "TEST");
        configuration.addProperty(MessengerUtils.APP_SECRET_KEY, "TEST_SECRET");
        configuration.addProperty(MessengerUtils.HANDLE_REACTIONS_KEY, true);
        configuration.addProperty(MessengerUtils.HANDLE_DELIVERIES_KEY, true);
        configuration.addProperty(MessengerUtils.HANDLE_READ_KEY, true);
        configuration.addProperty(MessengerUtils.AUTO_MARK_SEEN_KEY, true);

        MessengerPlatform messengerPlatform = new MessengerPlatform();
        messengerPlatform.start(mockedXatkitBot, configuration);
        return messengerPlatform;
    }

    @Test(expected = NullPointerException.class)
    public void constructNullPlatform() throws NullPointerException {
        provider = new MessengerIntentProvider(null);
    }

    @Test(expected = NullPointerException.class)
    public void startNullConfiguration() {
        provider = new MessengerIntentProvider(platform);
        provider.start(null);
    }

    @Test
    public void startValidConfiguration() {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
    }

    @Test
    public void getEndpointURI() {
        provider = new MessengerIntentProvider(platform);
        assertThat(provider.getEndpointURI())
                .as("Endpoint URI doesn't match the one defined in MessengerUtils")
                .isEqualTo(MessengerUtils.WEBHOOK_URI);
    }

    @Test
    public void handleMessage() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(CORRECT_CONTENT, platform.getAppSecret()),
                new ArrayList<>(),
                CORRECT_CONTENT);
        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(1)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition().getName()).isEqualTo(VALID_EVENT_DEFINITION.getName());
        verify(mockedXatkitBot, times(1)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void verifyMessageHasRawText() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(CORRECT_CONTENT, platform.getAppSecret()),
                new ArrayList<>(),
                CORRECT_CONTENT);
        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(1)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.RAW_TEXT_KEY)).isEqualTo(MESSAGE_TEXT);
        assertThat(sentEvent.getDefinition().getName()).isEqualTo(VALID_EVENT_DEFINITION.getName());
        verify(mockedXatkitBot, times(1)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void verify200ResponseForTextlessMessage() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        val response = provider.createRestHandler().handleContent(
                generateHeaders(TEXTLESS_MESSAGE, platform.getAppSecret()),
                new ArrayList<>(),
                TEXTLESS_MESSAGE);
        assertThat(response).isInstanceOf(HttpEntity.class);
    }

    @Test
    public void handleMultipleMessages() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(CORRECT_CONTENT_MULTIPLE_MESSAGES, platform.getAppSecret()),
                new ArrayList<>(),
                CORRECT_CONTENT_MULTIPLE_MESSAGES);
        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(2)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition().getName()).isEqualTo(VALID_EVENT_DEFINITION.getName());
        verify(mockedXatkitBot, times(2)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void handleMultipleEntriesWithMessages() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(CORRECT_CONTENT_MULTIPLE_ENTRIES, platform.getAppSecret()),
                new ArrayList<>(),
                CORRECT_CONTENT_MULTIPLE_ENTRIES);
        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(2)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition().getName()).isEqualTo(VALID_EVENT_DEFINITION.getName());
        verify(mockedXatkitBot, times(2)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void handleReaction() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(REACTION_MESSAGE, platform.getAppSecret()),
                new ArrayList<>(),
                REACTION_MESSAGE);
        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(1)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition()).isEqualTo(MessengerIntentProvider.MessageReact);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.MESSAGE_ID_KEY)).isEqualTo(MESSAGE_ID);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.REACTION_KEY)).isEqualTo(REACTION);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.EMOJI_KEY)).isEqualTo(EMOJI);
        verify(mockedXatkitBot, times(1)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void handleUnreaction() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(UNREACTION_MESSAGE, platform.getAppSecret()),
                new ArrayList<>(),
                UNREACTION_MESSAGE);
        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(1)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition()).isEqualTo(MessengerIntentProvider.MessageUnreact);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.MESSAGE_ID_KEY)).isEqualTo(MESSAGE_ID);
        verify(mockedXatkitBot, times(1)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void handleRead() throws NoSuchAlgorithmException, InvalidKeyException, RestHandlerException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(READ_MESSAGE, platform.getAppSecret()),
                new ArrayList<>(),
                READ_MESSAGE);

        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(1)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition()).isEqualTo(MessengerIntentProvider.MessageRead);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.WATERMARK_KEY)).isEqualTo(WATERMARK);
        verify(mockedXatkitBot, times(1)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test
    public void handleDelivered() throws NoSuchAlgorithmException, InvalidKeyException, RestHandlerException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(DELIVERED_MESSAGE, platform.getAppSecret()),
                new ArrayList<>(),
                DELIVERED_MESSAGE);

        ArgumentCaptor<EventInstance> eventCaptor = ArgumentCaptor.forClass(EventInstance.class);
        verify(mockedExecutionService, times(1)).handleEventInstance(eventCaptor.capture(), any(StateContext.class));
        EventInstance sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getDefinition()).isEqualTo(MessengerIntentProvider.MessageDelivered);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.WATERMARK_KEY)).isEqualTo(WATERMARK);
        assertThat(sentEvent.getPlatformData().get(MessengerUtils.MESSAGE_IDS_KEY)).asList().contains(MESSAGE_ID);
        verify(mockedXatkitBot, times(1)).getOrCreateContext(eq(SENDER_ID));
    }

    @Test(expected = RestHandlerException.class)
    public void handleIncorrectRequest() throws RestHandlerException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleContent(
                generateHeaders(INCORRECT_CONTENT, platform.getAppSecret()),
                new ArrayList<>(),
                INCORRECT_CONTENT);
        verify(mockedExecutionService, never()).handleEventInstance(any(EventInstance.class), any(StateContext.class));
    }

    @Test(expected = RestHandlerException.class)
    public void handleNullContentRequest() throws RestHandlerException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        provider.createRestHandler().handleParsedContent(new ArrayList<>(), new ArrayList<>(), null);
    }

    @Test(expected = RestHandlerException.class)
    public void handleIntentRecognitionProviderException() throws RestHandlerException, IntentRecognitionProviderException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        when(mockedIntentRecognitionProvider.getIntent(any(String.class), any(StateContext.class))).thenThrow(IntentRecognitionProviderException.class);
        provider.createRestHandler().handleContent(
                generateHeaders(CORRECT_CONTENT, platform.getAppSecret()),
                new ArrayList<>(),
                CORRECT_CONTENT);
    }

    @Test(expected = RestHandlerException.class)
    public void handleNoXHubSignature() throws RestHandlerException, IntentRecognitionProviderException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        when(mockedIntentRecognitionProvider.getIntent(any(String.class), any(StateContext.class))).thenThrow(IntentRecognitionProviderException.class);
        provider.createRestHandler().handleContent(
                new ArrayList<>(),
                new ArrayList<>(),
                CORRECT_CONTENT);
    }

    @Test(expected = RestHandlerException.class)
    public void handleIncorrectXHubSignature() throws RestHandlerException, IntentRecognitionProviderException, NoSuchAlgorithmException, InvalidKeyException {
        provider = new MessengerIntentProvider(platform);
        provider.start(configuration);
        when(mockedIntentRecognitionProvider.getIntent(any(String.class), any(StateContext.class))).thenThrow(IntentRecognitionProviderException.class);
        provider.createRestHandler().handleContent(
                generateHeaders(INCORRECT_CONTENT, platform.getAppSecret()),
                new ArrayList<>(),
                CORRECT_CONTENT);
    }


}
