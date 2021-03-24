package be.ledio.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import be.ledio.model.CartItem;
import be.ledio.model.ShoppingCart;

@Transactional
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
	// Hibernate parse the Table automatically for a maching ShoppingCart...
	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);
}
