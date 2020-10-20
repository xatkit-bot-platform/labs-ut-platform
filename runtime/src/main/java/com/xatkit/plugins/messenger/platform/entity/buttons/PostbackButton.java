package com.xatkit.plugins.messenger.platform.entity.buttons;

import lombok.Getter;

public class PostbackButton extends Button {
    @Getter
    private final String title; //20 char limit, rest gets shotened with ...
    @Getter
    private final String payload; //1000 char limit


    //The use of this heavily depends on whether we take intent from the title or from the payload
    public PostbackButton(String title) {
        this(title, title);
    }

    public PostbackButton(String title, String payload) {
        super("postback");
        this.title = title;
        this.payload = payload;
    }
}
