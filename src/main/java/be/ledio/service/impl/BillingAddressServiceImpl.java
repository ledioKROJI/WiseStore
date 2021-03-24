package be.ledio.service.impl;

import org.springframework.stereotype.Service;

import be.ledio.model.BillingAddress;
import be.ledio.model.UserBilling;
import be.ledio.service.BillingAddressService;

@Service
public class BillingAddressServiceImpl implements BillingAddressService {

	public BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress) {
		billingAddress.setBillingAddressName(userBilling.getUserBillingName());
		billingAddress.setBillingAddressStreet1(userBilling.getUserBillingStreet1());
		billingAddress.setBillingAddressStreet2(userBilling.getUserBillingStreet2());
		billingAddress.setBillingAddressCity(userBilling.getUserBillingCity());
		billingAddress.setBillingAddressState(userBilling.getUserBillingCountry());
		billingAddress.setBillingAddressCountry(userBilling.getUserBillingCountry());
		billingAddress.setBillingAddressZipcode(userBilling.getUserBillingZipcode());

		return billingAddress;
	}

}