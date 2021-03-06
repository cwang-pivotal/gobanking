package com.dugancathal.javabanking;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class PurchaseControllerTest {

	private PurchaseController purchaseController;
	private ReceiptRepository receiptRepository;
	private FakeCartService cartService;
	private FakeProductService productService;
	private Map<String, Money> prices;
	private FakeBankService bankService;
	private	Map<String,String> body;


	@Before
	public void setup() {
		body = new HashMap<>();
		prices = new HashMap<>();
		body.put("account_id", "4");

		prices.put("foo-id", new Money(600));
		prices.put("bar-id", new Money(400));

		cartService = new FakeCartService();
		productService = new FakeProductService();
		bankService = new FakeBankService();
		receiptRepository = new ReceiptRepository();
		purchaseController = new PurchaseController(receiptRepository, cartService, productService, bankService);
	}

	@Test
	public void checkout_retrievesItemsFromCart() {
		cartService.setItemIds(Arrays.asList("foo-id"));
		productService.setProductPrices(prices);

		purchaseController.checkout(body);

		assertEquals("foo-id", productService.getProductPriceWasCalledWith);
	}

	@Test
	public void checkout_calculatesSubtotalByProductPrices() {
		cartService.setItemIds(Arrays.asList("foo-id", "bar-id"));
		productService.setProductPrices(prices);

		Receipt receipt = purchaseController.checkout(body);

		assertEquals(new Money(1000), receipt.getSubtotal());
	}

	@Test
	public void checkout_calculatesTaxBasedOnSubtotal() {
		cartService.setItemIds(Arrays.asList("foo-id", "bar-id"));
		productService.setProductPrices(prices);

		Receipt receipt = purchaseController.checkout(body);

		assertEquals(new Money(80), receipt.getTax());
	}

	@Test
	public void checkout_calculatesTotalBasedOnSubtotalAndTax() {
		cartService.setItemIds(Arrays.asList("foo-id", "bar-id"));
		productService.setProductPrices(prices);

		Receipt receipt = purchaseController.checkout(body);

		assertEquals(new Money(1080), receipt.getTotal());
	}

	@Test
	public void checkout_savesTheReceipt() {
		cartService.setItemIds(Arrays.asList("foo-id", "bar-id"));
		productService.setProductPrices(prices);

		Receipt receipt = purchaseController.checkout(body);

		assertEquals(receipt, purchaseController.getReceipt(receipt.getId()));
	}

	@Test
	public void checkout_withdrawsTheTotalFromTheSpecifiedBank() {
		cartService.setItemIds(Arrays.asList("foo-id", "bar-id"));
		productService.setProductPrices(prices);

		purchaseController.checkout(body);

		assertEquals("4", bankService.withdrawalMadeAgainstAccountId);
		assertEquals(10.80, bankService.withdrawalAmount.getMoney(), 0.001);
	}


	class FakeCartService implements CartService {
		public boolean getWasCalled = false;
		public List<String> itemsToReturn = emptyList();

		public void setItemIds(List<String> items) {
			itemsToReturn = items;
		}

		public List<String> getItemIds() {
			getWasCalled = true;
			return itemsToReturn;
		}
	}

	private class FakeProductService implements ProductService {
		public String getProductPriceWasCalledWith;
		private Map<String, Money> productPrices;

		@Override
		public Money getProductPrice(String productId) {
			getProductPriceWasCalledWith =  productId;
			return productPrices.get(productId);
		}

		public void setProductPrices(Map<String, Money> productPrices) {
			this.productPrices = productPrices;
		}
	}

	private class FakeBankService implements BankService {
		public String withdrawalMadeAgainstAccountId;
		public Money withdrawalAmount;

		@Override
		public void makeWithdrawal(String accountId, Money amount) {
			withdrawalAmount = amount;
			withdrawalMadeAgainstAccountId = accountId;
		}
	}
}