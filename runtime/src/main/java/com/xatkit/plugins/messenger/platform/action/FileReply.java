package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.MessengerUtils;
import com.xatkit.plugins.messenger.platform.entity.File;
import com.xatkit.plugins.messenger.platform.entity.Message;
import com.xatkit.plugins.rest.platform.action.JsonRestRequest;
import lombok.val;
import org.apache.http.HttpHeaders;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileReply extends JsonRestRequest<JsonElement> {
    private static final Gson gson = new Gson();
    private final File file;

    /**
     * Constructs a POST Json request with form data parameters
     *
     * @param platform     the {@link MessengerPlatform} containing this action
     * @param context      the {@link StateContext} associated to this action
     * @param file         the information related to the file to be sent;
     */
    public FileReply(MessengerPlatform platform, StateContext context, File file) {
        super(platform, context, MethodKind.POST, MessengerUtils.ATTACHMENT_UPLOAD_API_URL, null, null, null, generateHeaders(platform), generateParams(file));
        this.file = file;
    }

    private static Map<String, String> generateHeaders(MessengerPlatform platform) {
        val headers = new HashMap<String, String>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + platform.getAccessToken());
        return headers;
    }

    private static Map<String, Object> generateParams(File file) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("message", gson.toJsonTree(new Message(file.getAttachment())));
        return params;
    }

    protected HttpRequest buildRequest() {
        return Unirest
                .post(this.restEndpoint)
                .headers(this.headers)
                .fields(this.formParameters)
                .field("filedata", file.getFile(), file.getMimeType())
                .getHttpRequest();
    }
}
