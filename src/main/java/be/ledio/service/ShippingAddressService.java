package be.ledio.service;

import be.ledio.model.ShippingAddress;
import be.ledio.model.UserShipping;

public interface ShippingAddressService {
	ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress);
}
