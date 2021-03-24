package be.ledio.service;

import java.util.Optional;

import be.ledio.model.UserPayment;

public interface UserPaymentService {
	Optional<UserPayment> findById(Long id);
	
	UserPayment save(UserPayment userPayment);
	
	void deleteById(Long id);
}
