package com.xatkit.plugins.messenger.platform.entity.buttons;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;


/**
 * A special type of button for general templates. Redirects the user to given URL.
 */
public class DefaultActionButton extends Button {
    @Getter
    private final String url;
    /**
     * Changes the button's height ratio to one of {@link WebviewHeightRatio}.
     */
    @SerializedName("webview_height_ratio")
    @Getter
    private final WebviewHeightRatio webviewHeightRatio;


    public DefaultActionButton(String url) {
        this(url, WebviewHeightRatio.full);
    }

    public DefaultActionButton(String url, WebviewHeightRatio webviewHeightRatio) {
        super("web_url");
        this.url = url;
        this.webviewHeightRatio = webviewHeightRatio;
    }


}
