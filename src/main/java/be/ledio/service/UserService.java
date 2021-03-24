package be.ledio.service;

import java.util.Set;

import be.ledio.model.User;
import be.ledio.model.UserBilling;
import be.ledio.model.UserPayment;
import be.ledio.model.UserShipping;
import be.ledio.model.security.PasswordResetToken;
import be.ledio.model.security.UserRole;

// Just here to allorw Dependency Injection...
public interface UserService {
	PasswordResetToken getPasswordResetToken(final String token);

	void createPasswordResetTokenForUser(final User user, final String token);

	User findByUsername(String username);

	User findByEmail(String Email);
	
	User findById(Long id);

	User createUser(User user, Set<UserRole> userRoles) throws Exception;

	User save(User user);

	User updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user);
	
	User setUserDefaultCreditCard(Long defaultUserPaymentId, User user);

	User updateUserShipping(UserShipping userShipping, User user);
	
	User setUserDefaultShippingAddress(Long defaultUserShippingId, User user);
}
