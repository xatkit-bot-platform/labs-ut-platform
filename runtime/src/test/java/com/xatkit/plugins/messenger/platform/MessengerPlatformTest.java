package com.xatkit.plugins.messenger.platform;

import com.xatkit.AbstractPlatformTest;
import com.xatkit.core.server.HttpMethod;
import com.xatkit.core.server.RestHandler;
import com.xatkit.core.server.RestHandlerException;
import com.xatkit.core.server.XatkitServer;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.xatkit.plugins.messenger.platform.MessengerPlatformTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class MessengerPlatformTest extends AbstractPlatformTest<MessengerPlatform> {
    private XatkitServer mockedXatkitServer;

    @Before
    public void setUp() {
        super.setUp();
        mockedXatkitServer = mock(XatkitServer.class);
        when(mockedXatkitBot.getXatkitServer()).thenReturn(mockedXatkitServer);
        platform = new MessengerPlatform();
    }

    private void setUpCorrectConfiguration() {
        configuration.addProperty(MessengerUtils.ACCESS_TOKEN_KEY, "TEST_ACCESS");
        configuration.addProperty(MessengerUtils.VERIFY_TOKEN_KEY, VERIFY_TOKEN);
        configuration.addProperty(MessengerUtils.APP_SECRET_KEY, "TEST_SECRET");
    }

    @Test
    public void startCorrectConfiguration() {
        setUpCorrectConfiguration();
        platform.start(mockedXatkitBot, configuration);
    }

    @Test(expected = Exception.class)
    public void startIncorrectConfiguration() {
        platform.start(mockedXatkitBot, configuration);
    }

    @Test(expected = Exception.class)
    public void startNullConfiguration() {
        platform.start(mockedXatkitBot, configuration);
    }

    @Test
    public void handleVerification() throws RestHandlerException, IOException {
        setUpCorrectConfiguration();
        platform.start(mockedXatkitBot, configuration);

        ArgumentCaptor<RestHandler> handlerCaptor = ArgumentCaptor.forClass(RestHandler.class);
        verify(mockedXatkitServer, times(1)).registerRestEndpoint(eq(HttpMethod.GET), eq(MessengerUtils.WEBHOOK_URI), handlerCaptor.capture());
        val restHandler = handlerCaptor.getValue();

        val response = restHandler.handleContent(new ArrayList<>(), VALIDATION_PARAMS, null);
        assertThat(response).isInstanceOf(StringEntity.class);
        val challenge = IOUtils.toString(((StringEntity) response).getContent(), StandardCharsets.UTF_8);
        assertThat(challenge).isEqualTo(CHALLENGE);
    }

    @Test(expected = RestHandlerException.class)
    public void handleIncorrectVerification1() throws RestHandlerException, IOException {
        setUpCorrectConfiguration();
        platform.start(mockedXatkitBot, configuration);

        ArgumentCaptor<RestHandler> handlerCaptor = ArgumentCaptor.forClass(RestHandler.class);
        verify(mockedXatkitServer, times(1)).registerRestEndpoint(eq(HttpMethod.GET), eq(MessengerUtils.WEBHOOK_URI), handlerCaptor.capture());
        val restHandler = handlerCaptor.getValue();

        restHandler.handleContent(new ArrayList<>(), INCORRECT_TOKEN_VALIDATION_PARAMS, null);
    }

    @Test(expected = RestHandlerException.class)
    public void handleIncorrectVerification2() throws RestHandlerException, IOException {
        setUpCorrectConfiguration();
        platform.start(mockedXatkitBot, configuration);

        ArgumentCaptor<RestHandler> handlerCaptor = ArgumentCaptor.forClass(RestHandler.class);
        verify(mockedXatkitServer, times(1)).registerRestEndpoint(eq(HttpMethod.GET), eq(MessengerUtils.WEBHOOK_URI), handlerCaptor.capture());
        val restHandler = handlerCaptor.getValue();

        restHandler.handleContent(new ArrayList<>(), INCORRECT_TYPE_VALIDATION_PARAMS, null);
    }
}
