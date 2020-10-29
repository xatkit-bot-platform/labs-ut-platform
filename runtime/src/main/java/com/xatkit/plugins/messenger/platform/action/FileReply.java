package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.Gson;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.MessengerPlatform;
import com.xatkit.plugins.messenger.platform.MessengerUtils;
import com.xatkit.plugins.messenger.platform.entity.DirectFile;
import com.xatkit.plugins.rest.platform.action.PostJsonRequestWithFormData;
import lombok.val;
import org.apache.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public class FileReply extends PostJsonRequestWithFormData {
    private static final Gson gson = new Gson();

    /**
     * Constructs a POST Json request with form data parameters
     *
     * @param platform     the {@link MessengerPlatform} containing this action
     * @param context      the {@link StateContext} associated to this action
     * @param file         the information related to the file to be sent;
     */
    public FileReply(MessengerPlatform platform, StateContext context, DirectFile file) {
        super(platform, context, MessengerUtils.SEND_API_URL, null, null, generateHeaders(platform), file.getParams());
    }

    private static Map<String, String> generateHeaders(MessengerPlatform platform) {
        val headers = new HashMap<String, String>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + platform.getAccessToken());
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        return headers;
    }
}
