package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Message {

    private String text;
    private String attachmentType;
    private String payloadURL;
    private boolean reusablePayload;

    // TODO: Support files and templates as well
    public enum attachmentType {
        audio,
        video,
        image
    }

    public Message() {
        this.text = null;
        this.attachmentType = null;
        this.payloadURL = null;
        this.reusablePayload = false;
    }

    public Message text(String text) {
        this.attachmentType = null;
        this.payloadURL = null;
        this.reusablePayload = false;

        this.text = text;
        return this;
    }

    public Message attachment(attachmentType type, String url, boolean reusable) {
        String typeString = "";
        switch (type) {
            case audio: typeString = "audio"; break;
            case video: typeString = "video"; break;
            case image: typeString = "image"; break;
        }
        return attachment(typeString,url,reusable);
    }

    public Message attachment(String type, String url, boolean reusable) {
        this.text = null;

        this.attachmentType = type;
        this.payloadURL = url;
        this.reusablePayload = reusable;
        return this;
    }

    public JsonObject getJson() {
        JsonObject messageObject = new JsonObject();

        if (text != null) {
            messageObject.add("text", new JsonPrimitive(text));
        }

        if (attachmentType != null && payloadURL != null) {
            JsonObject attachmentObject = new JsonObject();
            attachmentObject.addProperty("type", attachmentType);
                JsonObject payloadObject = new JsonObject();
                payloadObject.addProperty("url", payloadURL);
                payloadObject.addProperty("is_reusable", reusablePayload);
            attachmentObject.add("payload",payloadObject);
            messageObject.add("attachment",attachmentObject);
        }

        return messageObject;
    }

}
