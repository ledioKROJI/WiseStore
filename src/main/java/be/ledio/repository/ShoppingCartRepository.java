package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;

import be.ledio.model.ShoppingCart;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> {

}
