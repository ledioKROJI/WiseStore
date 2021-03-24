package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import be.ledio.model.UserPayment;


public interface UserPaymentRepository extends CrudRepository<UserPayment, Long>{

}
