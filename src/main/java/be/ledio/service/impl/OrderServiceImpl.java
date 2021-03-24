package be.ledio.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.BillingAddress;
import be.ledio.model.Book;
import be.ledio.model.CartItem;
import be.ledio.model.Order;
import be.ledio.model.Payment;
import be.ledio.model.ShippingAddress;
import be.ledio.model.ShoppingCart;
import be.ledio.model.User;
import be.ledio.repository.OrderRepository;
import be.ledio.service.CartItemService;
import be.ledio.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartItemService cartItemService;

	@Override
	public synchronized Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress,
			BillingAddress billingAddress, Payment payment, String shippingMethod, User user) {
		// Create a new instance and fill it with object that come from the checkout
		// page...
		Order order = new Order();
		order.setBillingAddress(billingAddress);
		order.setOrderStatus("created");
		order.setPayment(payment);
		order.setShippingAddress(shippingAddress);
		order.setShippingMethod(shippingMethod);

		// Set this Order object for each CartItem in the ShoppingCart...
		// It also calculates the remaining book in the stock by substracting CartItem
		// Qty to the Book's StockNumber
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		for (CartItem cartItem : cartItemList) {
			Book book = cartItem.getBook();
			cartItem.setOrder(order);
			book.setInStockNumber(book.getInStockNumber() - cartItem.getQty());
		}

		order.setCartItemList(cartItemList);
		order.setOrderDate(LocalDate.now());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		shippingAddress.setOrder(order);
		billingAddress.setOrder(order);
		payment.setOrder(order);
		order.setUser(user);
		
		return orderRepository.save(order);
	}

	@Override
	public Order findById(Long id) {
		return orderRepository.findById(id).get();
	}
}
