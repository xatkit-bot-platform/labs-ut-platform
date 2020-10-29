package com.xatkit.plugins.messenger.platform.entity;
import com.google.gson.Gson;
import com.xatkit.plugins.messenger.platform.entity.payloads.FilePayload;
import fr.inria.atlanmod.commons.log.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class DirectFile extends File {
    private static final Gson gson = new Gson();

    private Recipient recipient;

    public DirectFile(String recipientId, File file) {
        super(file);
        this.recipient = new Recipient(recipientId);
    }

    public DirectFile(String recipientId, Attachment.AttachmentType attachmentType, java.io.File file, String fileExtension) {
        super(attachmentType,file,fileExtension);
        this.recipient = new Recipient(recipientId);
    }

    public Map<String, Object> getParams() {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("recipient",gson.toJsonTree(new Messaging(recipient)));
        params.putAll(super.getParams());
        return params;
    }
}
