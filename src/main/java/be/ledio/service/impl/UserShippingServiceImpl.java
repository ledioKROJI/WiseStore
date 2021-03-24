package be.ledio.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.UserShipping;
import be.ledio.repository.UserShippingRepository;
import be.ledio.service.UserShippingService;

@Service
public class UserShippingServiceImpl implements UserShippingService {

	@Autowired
	UserShippingRepository userShippingRepository;

	@Override
	public Optional<UserShipping> findById(Long id) {
		return userShippingRepository.findById(id);
	}
	
	@Override
	public UserShipping save(UserShipping userShipping) {
		return userShippingRepository.save(userShipping);
	}

	@Override
	public void remove(Long id) {
		userShippingRepository.deleteById(id);
	}
}
