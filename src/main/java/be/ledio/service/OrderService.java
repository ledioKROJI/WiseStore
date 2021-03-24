package be.ledio.service;

import be.ledio.model.BillingAddress;
import be.ledio.model.Order;
import be.ledio.model.Payment;
import be.ledio.model.ShippingAddress;
import be.ledio.model.ShoppingCart;
import be.ledio.model.User;

public interface OrderService {
	Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, BillingAddress billingAddress,
			Payment payment, String shippingMethod, User user);

	Order findById(Long id);
}
