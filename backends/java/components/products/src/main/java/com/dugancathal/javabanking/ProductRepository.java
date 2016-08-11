package com.dugancathal.javabanking;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

@Component
public class ProductRepository {
	private HashMap products;
	private Integer counter;

	public ProductRepository() {
		resetProducts();
	}

	public Product find(String idOrName) {
		return catalog().get(idOrName);
	}

	public Collection<Product> all() {
		return catalog().values().stream().collect(toSet());
	}

	private Map<String, Product> catalog() {
		return products;
	}

	public Product createProduct(Product product) {
		counter++;
		product.setId(counter.toString());
		products.put(product.getId(), product);
		products.put(product.getName(), product);
		return product;
	}

	public void resetProducts() {
		products = new HashMap<>();
		counter = 1;
		Product teddyBear = new Product(counter.toString(), "TeddyBear", "Soft and Cuddly", new Money(999));
		products.put(teddyBear.getId(), teddyBear);
		products.put(teddyBear.getName(), teddyBear);
	}

	public void deleteProduct(Product product) {
		products.remove(product.getId(), product);
		products.remove(product.getName(), product);
	}
}