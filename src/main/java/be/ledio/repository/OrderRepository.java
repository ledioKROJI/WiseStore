package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;

import be.ledio.model.Order;

public interface OrderRepository extends CrudRepository<Order, Long>{

}
