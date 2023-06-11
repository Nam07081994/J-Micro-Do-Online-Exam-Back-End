package com.example.demo.common.generator;

import java.util.UUID;

public class PasswordGenerator {

	public static String generateRandomPassword() {
		return UUID.randomUUID().toString();
	}
}
