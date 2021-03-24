package be.ledio.service;

import be.ledio.model.Payment;
import be.ledio.model.UserPayment;

public interface PaymentService {
	Payment setByUserPayment(UserPayment userPayment, Payment payment);
}
