package be.ledio.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.UserBilling;
import be.ledio.repository.UserBillingRepository;
import be.ledio.service.UserBillingService;

@Service
public class UserBillingServiceImpl implements UserBillingService{

	@Autowired
	UserBillingRepository userBillingRepository;

	@Override
	public Optional<UserBilling> findById(Long id) {
		return userBillingRepository.findById(id);
	}

	@Override
	public UserBilling save(UserBilling userBilling) {
		return userBillingRepository.save(userBilling);
	}
}
