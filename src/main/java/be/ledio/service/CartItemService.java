package be.ledio.service;

import java.util.List;

import be.ledio.model.Book;
import be.ledio.model.CartItem;
import be.ledio.model.ShoppingCart;
import be.ledio.model.User;

public interface CartItemService {

	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);

	CartItem updateCartItem(CartItem cartItem);

	CartItem addBookToCartItem(Book book, User user, int qty);

	CartItem findById(Long id);

	void removeCartItem(CartItem cartItem);
	
	CartItem save(CartItem cartItem);
}
