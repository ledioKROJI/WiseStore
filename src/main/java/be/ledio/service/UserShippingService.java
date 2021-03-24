package be.ledio.service;

import java.util.Optional;

import be.ledio.model.UserShipping;

public interface UserShippingService {
	
	Optional<UserShipping> findById(Long id);
	
	UserShipping save(UserShipping userShipping);
	
	void remove(Long id);

}
