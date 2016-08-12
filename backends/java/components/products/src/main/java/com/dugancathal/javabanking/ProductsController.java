package com.dugancathal.javabanking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductsController {
	@Autowired
	private ProductRepository productRepository;

	@RequestMapping(path="/products", method= RequestMethod.GET)
	public List<Product> getProducts(@RequestParam(name="sort", required = false) String sort) {
		return productRepository.getAll(sort);
	}

	@RequestMapping(path="/products/{id}", method= RequestMethod.GET)
	public Product getProduct(@PathVariable String id) {
        Product product = productRepository.find(id);
        if (product != null) {
            return product;
        }
        throw new ResourceNotFoundException();
    }

    @ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path="/products", method= RequestMethod.POST)
	public Product createProduct(@RequestBody Product product) {
		return productRepository.createProduct(product);
	}

    @RequestMapping(path="/products", method= RequestMethod.DELETE)
    public void deleteProducts() {
        productRepository.resetProducts();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path="/products/{id}", method= RequestMethod.DELETE)
    public void deleteProduct(@PathVariable String id) {
        Product product = productRepository.find(id);
        if (product != null) {
            productRepository.deleteProduct(product);
            return;
        }
        throw new ResourceNotFoundException();
    }
}