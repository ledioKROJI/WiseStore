package be.ledio.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.UserPayment;
import be.ledio.repository.UserPaymentRepository;
import be.ledio.service.UserPaymentService;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

	@Autowired
	private UserPaymentRepository userPaymentRepository;

	@Override
	public Optional<UserPayment> findById(Long id) {
		return userPaymentRepository.findById(id);
	}

	@Override
	public UserPayment save(UserPayment userPayment) {
		return userPaymentRepository.save(userPayment);
	}

	@Override
	public void deleteById(Long id) {
		userPaymentRepository.deleteById(id);
	}
}
