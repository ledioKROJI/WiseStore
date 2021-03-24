package be.ledio.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import be.ledio.model.BookToCartItem;
import be.ledio.model.CartItem;

@Transactional
public interface BookToCartItemRepository extends CrudRepository<BookToCartItem, Long> {
	void deleteByCartItem(CartItem cartItem);
}
