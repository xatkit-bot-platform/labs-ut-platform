package com.xatkit.plugins.messenger.platform.entity;
import com.google.gson.Gson;
import com.xatkit.plugins.messenger.platform.entity.payloads.FilePayload;
import fr.inria.atlanmod.commons.log.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class File {
    private static final Gson gson = new Gson();

    private Attachment attachment;
    private java.io.File file;
    private String type;

    public File(Attachment.AttachmentType attachmentType, java.io.File file, String fileExtension) {
        String type = attachmentType.name();
        if (fileExtension != null) type += "/" + fileExtension.toLowerCase();
        this.type = type;
        Log.debug("TYPE: {0}", type);
        this.file = file;
        this.attachment = new Attachment(attachmentType,new FilePayload(true));
    }

    public File(File file) {
        this.attachment = file.getAttachment();
        this.file = file.getFile();
        this.type = file.getType();
    }

    public Attachment getAttachment() { return attachment; }
    public java.io.File getFile() { return file; }
    public String getType() { return type; }

    public Map<String, Object> getParams() {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("message",gson.toJsonTree(new FileSending(attachment)));
        params.put("filedata",file);
        params.put("type",type);
        return params;
    }
}
