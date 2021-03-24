package be.ledio.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import be.ledio.model.Book;
import be.ledio.model.CartItem;
import be.ledio.model.ShoppingCart;
import be.ledio.model.User;
import be.ledio.service.BookService;
import be.ledio.service.CartItemService;
import be.ledio.service.ShoppingCartService;
import be.ledio.service.UserService;

@Controller
public class ShoppingCartController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private BookService bookService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@RequestMapping("/cart")
	public String shoppingCart(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		ShoppingCart shoppingCart = user.getShoppingCart();

		// It only retrives a list of CartItems linked to the ShoppingCart...
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		// Here we have the ShoppingCart of the user with the updated sum of books
		// prices
		shoppingCart = shoppingCartService.updateShoppingCart(shoppingCart);

		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);

		return "shopping-cart";
	}

	@RequestMapping(value = "/add-cart-item", method = RequestMethod.POST)
	public String addItem(@ModelAttribute("bookId") Long bookId, @ModelAttribute("qty") String qty, Model model,
			Principal principal) {
//		Long localBookId = Long.valueOf(bookId);
		User user = userService.findByUsername(principal.getName());
		Book book = bookService.findById(bookId).get();

		if (Integer.parseInt(qty) > book.getInStockNumber()) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/book-detail?id=" + book.getId();
		}

		cartItemService.addBookToCartItem(book, user, Integer.parseInt(qty));
		model.addAttribute("addBookSuccess", true);
		return "forward:/book-detail?id=" + book.getId();
	}

	@RequestMapping("/update-cart-item")
	public String updateShoppingCart(@ModelAttribute("id") Long cartItemId, @ModelAttribute("qty") int qty) {
		CartItem cartItem = cartItemService.findById(cartItemId);
		cartItem.setQty(qty);
		cartItemService.updateCartItem(cartItem);

		return "forward:/cart";
	}

	@RequestMapping("/remove-item")
	public String removeItem(@RequestParam("id") Long id) {
		cartItemService.removeCartItem(cartItemService.findById(id));

		return "forward:/cart";
	}
}
