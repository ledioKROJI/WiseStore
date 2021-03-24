package be.ledio.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.Book;
import be.ledio.model.BookToCartItem;
import be.ledio.model.CartItem;
import be.ledio.model.ShoppingCart;
import be.ledio.model.User;
import be.ledio.repository.BookToCartItemRepository;
import be.ledio.repository.CartItemRepository;
import be.ledio.service.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private BookToCartItemRepository bookToCartItemRepository;

	public List<CartItem> findByShoppingCart(ShoppingCart shoppingCart) {
		return cartItemRepository.findByShoppingCart(shoppingCart);
	}

	public CartItem updateCartItem(CartItem cartItem) {
		BigDecimal bigDecimal = new BigDecimal(cartItem.getBook().getOurPrice())
				.multiply(new BigDecimal(cartItem.getQty()));
		bigDecimal = bigDecimal.round(new MathContext(2, RoundingMode.HALF_UP));
		cartItem.setSubtotal(bigDecimal);

		return cartItemRepository.save(cartItem);
	}

	public CartItem addBookToCartItem(Book book, User user, int qty) {
		List<CartItem> cartItemList = findByShoppingCart(user.getShoppingCart());

		for (CartItem cartItem : cartItemList) {
			if (book.getId() == cartItem.getBook().getId()) {
				// If the CartItem with quantity of book already exists it only update this
				// quantity
				cartItem.setQty(cartItem.getQty() + qty);
				// re-calculate the total price based on the new quantity of books to buy
				cartItem.setSubtotal(new BigDecimal(book.getOurPrice()).multiply(new BigDecimal(cartItem.getQty())));
				return cartItemRepository.save(cartItem);
			}
		}

		CartItem cartItem = new CartItem();
		cartItem.setShoppingCart(user.getShoppingCart());
		cartItem.setBook(book);

		cartItem.setQty(qty);
		cartItem.setSubtotal(new BigDecimal(book.getOurPrice()).multiply(new BigDecimal(qty)));
		cartItem = cartItemRepository.save(cartItem);

		BookToCartItem bookToCartItem = new BookToCartItem();
		bookToCartItem.setBook(book);
		bookToCartItem.setCartItem(cartItem);
		bookToCartItemRepository.save(bookToCartItem);

		return cartItem;
	}

	public CartItem findById(Long id) {
		return cartItemRepository.findById(id).get();
	}

	public void removeCartItem(CartItem cartItem) {
		bookToCartItemRepository.deleteByCartItem(cartItem);
		cartItemRepository.delete(cartItem);
	}

	@Override
	public CartItem save(CartItem cartItem) {
		return cartItemRepository.save(cartItem);
	}
}
