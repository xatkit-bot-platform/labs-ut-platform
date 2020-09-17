package com.xatkit.plugins.messenger.platform;

import com.google.api.client.json.Json;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.xatkit.core.XatkitException;
import com.xatkit.core.server.RestHandler;
import com.xatkit.core.server.RestHandlerException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.List;

import static java.util.Objects.isNull;

public abstract class MessengerRestHandler extends RestHandler<Object> {
    private static JsonParser jsonParser = new JsonParser();

    @Override
    public final boolean acceptContentType(String contentType) {
        return ContentType.APPLICATION_JSON.getMimeType().equals(contentType);
    }

    @Override
    protected Object parseContent(@Nullable Object content) {
        if (isNull(content)) {
            return null;
        }
        if (content instanceof String) {
            return jsonParser.parse((String) content);
        }
        if (content instanceof Reader) {
            return jsonParser.parse((Reader) content);
        }
        if (content instanceof JsonReader) {
            return jsonParser.parse((JsonReader) content);
        }
        throw new XatkitException(MessageFormat.format("Cannot parse the provided content {0}, expected a {1}, {2}, " +
                "or {3}, found {4}", content, String.class.getName(), Reader.class.getName(), JsonReader.class
                .getName(), content.getClass().getName()));
    }

    @Override
    protected Object handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params, @Nullable Object content) throws RestHandlerException {
        return handleParsedContent(headers, params, (JsonElement) content);
    }

    public abstract HttpEntity handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params, @Nullable JsonElement content) throws RestHandlerException;
}
