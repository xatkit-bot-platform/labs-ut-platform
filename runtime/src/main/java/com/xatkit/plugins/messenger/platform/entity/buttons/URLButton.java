package com.xatkit.plugins.messenger.platform.entity.buttons;

import lombok.Getter;

/**
 * A button with a title. A subclass {@link DefaultActionButton} with the added title field. Redirects the user to given URL.
 */
public class URLButton extends DefaultActionButton {
    @Getter
    private final String title; //any title longer than 20 characters will get shortened with ...

    public URLButton(String url, String title) {
        super(url);
        this.title = title;
    }

    public URLButton(String url, String title, WebviewHeightRatio webviewHeightRatio) {
        super(url, webviewHeightRatio);
        this.title = title;
    }
}
