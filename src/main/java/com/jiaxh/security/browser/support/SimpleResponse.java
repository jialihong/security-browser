package com.jiaxh.security.browser.support;

/**
 * @Auther: jiaxh
 * @Date: 2019/5/7 14:57
 */
public class SimpleResponse {

    public SimpleResponse(Object content) {
        this.content = content;
    }

    private Object content;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
