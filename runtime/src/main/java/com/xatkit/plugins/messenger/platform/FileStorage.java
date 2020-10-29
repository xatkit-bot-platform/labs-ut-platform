package com.xatkit.plugins.messenger.platform;

import com.xatkit.execution.StateContext;
import com.xatkit.plugins.messenger.platform.entity.Attachment;
import com.xatkit.plugins.messenger.platform.entity.File;
import com.xatkit.plugins.messenger.platform.entity.ReusableFile;
import com.xatkit.plugins.messenger.platform.entity.payloads.AttachmentIDPayload;

import java.util.HashMap;

public class FileStorage {
    MessengerPlatform platform;
    HashMap<String, ReusableFile> files;

    public FileStorage(MessengerPlatform platform) {
        this.platform = platform;
        this.files = new HashMap<>();
    }

    public Attachment LazyAttachment(String filepath, StateContext context) {
        CreateAttachment(null, filepath, Attachment.AttachmentType.file, null, context);
        return RetrieveAttachment(filepath);
    }

    public Attachment RetrieveAttachment(String name) {
        if (!files.containsKey(name)) return null;

        ReusableFile file = files.get(name);
        Attachment.AttachmentType type = file.getFile().getAttachment().getType();
        AttachmentIDPayload payload = new AttachmentIDPayload(file.getAttachmentId());
        Attachment attachment = new Attachment(type,payload);

        return attachment;
    }

    public void CreateAttachment(String name, String filepath, Attachment.AttachmentType type, String extension, StateContext context) {
        if (files.containsKey(name)) return;

        File file = new File(type,new java.io.File(filepath),extension);
        ReusableFile reusableFile = new ReusableFile(file);
        if (name == null) name = filepath;
        files.put(name,reusableFile);

        platform.sendFile(context,reusableFile);
    }
}
