package be.ledio.service;

import java.util.Optional;

import be.ledio.model.UserBilling;

public interface UserBillingService {
	Optional<UserBilling> findById(Long id);

	UserBilling save(UserBilling userBilling);
}
