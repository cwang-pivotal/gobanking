package com.dugancathal.javabanking;

import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toSet;

@Component
public class ProductRepository {
	private HashMap<String, Product> productCatalog;
	private Integer counter;

    public ProductRepository() {
		resetProducts();
	}

	public Product find(String idOrName) {
		return productCatalog.get(idOrName);
	}

	public List<Product> getAll(String sort) {
        Set<Product> productSet = productCatalog.values().stream().collect(toSet());
        List productList = new ArrayList<Product>(productSet);
        productList.sort(new ProductComparator(sort));
        return productList;
    }

    public Product createProduct(Product product) {
		counter++;
		product.setId(counter.toString());
		productCatalog.put(product.getId(), product);
		productCatalog.put(product.getName(), product);
		return product;
	}

	public void resetProducts() {
		productCatalog = new HashMap<>();
		counter = 1;
		Product teddyBear = new Product(counter.toString(), "TeddyBear", "Soft and Cuddly", new Money(999));
		productCatalog.put(teddyBear.getId(), teddyBear);
		productCatalog.put(teddyBear.getName(), teddyBear);
	}

	public void deleteProduct(Product product) {
		productCatalog.remove(product.getId(), product);
		productCatalog.remove(product.getName(), product);
	}

    private class ProductComparator implements Comparator<Product> {
        private final String sort;

        public ProductComparator(String sort) {
            this.sort = sort;
        }

        @Override
        public int compare(Product one, Product two) {
            switch(this.sort) {
                case "name":
                    return one.getName().compareTo(two.getName());
                case "-name":
                    return two.getName().compareTo(one.getName());

            }
            return 0;
        }
    }
}