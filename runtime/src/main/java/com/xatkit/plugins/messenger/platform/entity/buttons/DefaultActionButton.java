package com.xatkit.plugins.messenger.platform.entity.buttons;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;


/**
 * A special type of button for general templates. Redirects the user to given URL.
 */
public class DefaultActionButton implements Button {

    @Getter
    private final String type = "web_url";
    @Getter
    private final String url;
    /**
     * Changes the button's height ratio to one of {@link WebviewHeightRatio}.
     */
    @SerializedName("webview_height_ratio")
    @Getter
    private final WebviewHeightRatio webviewHeightRatio;


    public DefaultActionButton(String url) {
        this.url = url;
        this.webviewHeightRatio = WebviewHeightRatio.full;
    }

    public DefaultActionButton(String url, WebviewHeightRatio webviewHeightRatio) {
        this.url = url;
        this.webviewHeightRatio = webviewHeightRatio;
    }


}
