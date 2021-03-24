package be.ledio.service;

import be.ledio.model.BillingAddress;
import be.ledio.model.UserBilling;

public interface BillingAddressService {
	BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress);
}
