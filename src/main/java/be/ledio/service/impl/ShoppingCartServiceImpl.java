package be.ledio.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.CartItem;
import be.ledio.model.ShoppingCart;
import be.ledio.repository.ShoppingCartRepository;
import be.ledio.service.CartItemService;
import be.ledio.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private ShoppingCartRepository shoppingCartRepository;

	// It generate the total price of all the cart based on the subtotal of each
	// cartIeam and their number of books
	public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {
		BigDecimal cartTotal = new BigDecimal(0);

		// It store all cartItems in the ShoppingCart...
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

		// It set the total price based on the number of book that are available (+ than
		// 0)
		for (CartItem cartItem : cartItemList) {
			if (cartItem.getBook().getInStockNumber() > 0) {
				cartTotal = cartTotal.add(cartItemService.updateCartItem(cartItem).getSubtotal());

				// Here I think that the right logic is that : cartTotal =
				// cartTotal.add(cartItemService.updateCartItem(cartItem).getSubtotal());
				// Because the updateCartItem does operation too and above this, there is
				// nothing that store the updated CartItem
			}
		}
		shoppingCart.setGrandTotal(cartTotal);
		return shoppingCartRepository.save(shoppingCart);
	}

	@Override
	public void clearShoppingCart(ShoppingCart shoppingCart) {
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

		// Here we are not deleting the CartItems because we need them is the Order
		// object... In order to we the summary page and our history
		for (CartItem cartItem : cartItemList) {
			cartItem.setShoppingCart(null);
			cartItemService.save(cartItem);
		}

		shoppingCart.setGrandTotal(new BigDecimal(0));

		shoppingCartRepository.save(shoppingCart);
	}
}
