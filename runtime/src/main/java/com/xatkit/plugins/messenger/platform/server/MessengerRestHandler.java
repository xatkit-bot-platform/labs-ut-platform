package com.xatkit.plugins.messenger.platform.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xatkit.core.XatkitException;
import com.xatkit.core.server.RestHandler;
import com.xatkit.core.server.RestHandlerException;
import lombok.Getter;
import lombok.NonNull;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;

import static java.util.Objects.isNull;

public abstract class MessengerRestHandler extends RestHandler<Object> {
    private static final JsonParser jsonParser = new JsonParser();

    @Override
    public final boolean acceptContentType(final String contentType) {
        return ContentType.APPLICATION_JSON.getMimeType().equals(contentType);
    }

    @Override
    protected Object parseContent(final @Nullable Object content) {
        if (isNull(content)) {
            return null;
        }
        if (content instanceof String) {
            return new MessengerContent(jsonParser.parse((String) content), (String) content);
        }

        throw new XatkitException(MessageFormat.format("Cannot parse the provided content {0}, expected a {1}, found {4}",
                content, String.class.getName(), content.getClass().getName()));
    }

    @Override
    protected Object handleParsedContent(
            final @Nonnull List<Header> headers,
            final @Nonnull List<NameValuePair> params,
            final @Nullable Object content) throws RestHandlerException {
        return handleParsedContent(headers, params, (MessengerContent) content);
    }


    public abstract HttpEntity handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params, @Nullable MessengerContent content) throws RestHandlerException;

    protected static class MessengerContent {
        @Getter
        private final JsonElement jsonElement;
        @Getter
        private final String rawContent;

        private MessengerContent(final @NonNull JsonElement jsonElement, final @NonNull String rawContent) {
            this.jsonElement = jsonElement;
            this.rawContent = rawContent;
        }

        @Override
        public String toString() {
            return rawContent;
        }
    }
}
