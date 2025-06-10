package com.readingshare.common.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 簡単な疎通確認用のエンドポイントを提供します。
 */
@RestController
public class HelloController {

    /**
     * "Hello, World!" を返す GET エンドポイント。
     *
     * @return 固定文字列 "Hello, World!"
     */
    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String hello() {
        return "Hello, World!";
    }
}
