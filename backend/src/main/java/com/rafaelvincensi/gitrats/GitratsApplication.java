package com.rafaelvincensi.gitrats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GitratsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitratsApplication.class, args);
	}

}
