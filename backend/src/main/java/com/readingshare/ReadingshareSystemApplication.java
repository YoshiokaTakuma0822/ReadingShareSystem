package com.readingshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ReadingShare System のメインアプリケーションクラス。
 * Spring Bootアプリケーションを起動する。
 */
@SpringBootApplication
@EnableScheduling
public class ReadingshareSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReadingshareSystemApplication.class, args);
    }
}
