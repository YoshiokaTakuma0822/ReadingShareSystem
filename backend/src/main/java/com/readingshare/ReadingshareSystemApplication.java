package com.readingshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * ReadingShare System のメインアプリケーションクラス。
 * Spring Bootアプリケーションを起動する。
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.readingshare") // すべてのパッケージをスキャン対象に
@EntityScan(basePackages = "com.readingshare") // Entityクラスをスキャン対象に
@EnableJpaRepositories(basePackages = "com.readingshare") // JPAリポジトリを有効化
public class ReadingshareSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadingshareSystemApplication.class, args);
    }

}