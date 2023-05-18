package com.ljx.example.test;

import java.io.Serializable;

/**
 * User: ljx
 * Date: 2022/3/22
 * Time: 17:15
 */
public class Test implements Serializable {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
