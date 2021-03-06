package com.dugancathal.javabanking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
public class HttpProductService implements ProductService {
	@Autowired
	private Environment env;
	private RestTemplate httpClient = new RestTemplate();
	private ObjectMapper jsonMapper = new ObjectMapper();

	@Override
	public Money getProductPrice(String productId) {
		String responseBody = httpClient.getForObject(String.format("%s/products/%s", url(), productId), String.class);
		try {
			Map<String, Object> body = jsonMapper.readValue(responseBody, new TypeReference<Map<String, Object>>(){});
			Map<?, ?> priceMap = (Map<?, ?>) body.get("price");
			int pennies = (int) ((Double)priceMap.get("money") * 100);
			return new Money(pennies);
		} catch (IOException e) {
			return null;
		}
	}

	private String url() {
		if(env.getProperty("PRODUCT_URL") != null) {
			return env.getProperty("PRODUCT_URL");
		} else {
			return "http://products.dev:8080";
		}
	}
}