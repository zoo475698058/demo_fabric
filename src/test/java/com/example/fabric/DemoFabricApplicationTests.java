package com.example.fabric;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootTest
class DemoFabricApplicationTests {

	@Test
	void contextLoads() throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = DemoFabricApplication.class.getResourceAsStream("/application.properties");
		properties.load(inputStream);

		String channelName = properties.getProperty("channelName");

		System.out.println(channelName);
	}

}
