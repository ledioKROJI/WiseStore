package be.ledio.service.impl;

import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ledio.model.ShoppingCart;
import be.ledio.model.User;
import be.ledio.model.UserBilling;
import be.ledio.model.UserPayment;
import be.ledio.model.UserShipping;
import be.ledio.model.security.PasswordResetToken;
import be.ledio.model.security.UserRole;
import be.ledio.repository.PasswordResetTokenRepository;
import be.ledio.repository.RoleRepository;
import be.ledio.repository.UserRepository;
import be.ledio.service.UserPaymentService;
import be.ledio.service.UserService;
import be.ledio.service.UserShippingService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserPaymentService userPaymentService;

	@Autowired
	private UserShippingService userShippingService;

	@Override
	// Find a PasswordResetToken entity in DB by the token field...
	public PasswordResetToken getPasswordResetToken(final String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(final User user, final String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		// This method is avaiable in the repository because it implements JpaRepo which
		// impl PagingAndSortingRepo which impl CrudRepo and which automate CRUD
		// operations...
		passwordResetTokenRepository.save(myToken);
	}

	@Override
	public User findByUsername(String username) {
		User user = userRepository.findByUserName(username);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		User user = userRepository.findByEmail(email);
		return user;
	}
	
	@Override
	public User findById(Long id) {
		return userRepository.findById(id).get();
	}

	@Override
	public User createUser(User user, Set<UserRole> userRoles) throws Exception {
		User localUser = userRepository.findByUserName(user.getUserName());

		// My fault == if (localUser.getUserName() != null) {
		// If above the localUser is null because the repo found nothing we can't
		// execute the "getUserName()"
		if (localUser != null) {
			LOG.info("user {} already exists. Nothing will be done.", user.getUsername());
		} else {
			// Add role to the "role" table from "UserRole" object
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}
			// Fill the userRoles field of user with new roles created from the Controller
			// class
			user.getUserRoles().addAll(userRoles);

			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setUser(user);
			user.setShoppingCart(shoppingCart);

			user.setUserShippingList(new ArrayList<UserShipping>());
			user.setUserPaymentList(new ArrayList<UserPayment>());

			// Finally save the user from the Controller...
			localUser = userRepository.save(user);
		}
		return localUser;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);
		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);
		return save(user);
	}

	@Override
	public User setUserDefaultCreditCard(Long defaultUserPaymentId, User user) {
		User localUser = userRepository.findById(user.getId()).get();

		for (UserPayment userPayment : localUser.getUserPaymentList()) {
			if (userPayment.getId() == defaultUserPaymentId) {
				userPayment.setDefaultPayment(true);
				userPaymentService.save(userPayment);
			} else {
				userPayment.setDefaultPayment(false);
				userPaymentService.save(userPayment);
			}
		}
		return userRepository.findById(user.getId()).get();
	}

	@Override
	public User updateUserShipping(UserShipping userShipping, User user) {
		userShipping.setUser(user);
		userShipping.setUserShippingDefault(true);
		user.getUserShippingList().add(userShipping);
		return save(user);
	}

	@Override
	public User setUserDefaultShippingAddress(Long defaultUserShippingId, User user) {

		User localUser = userRepository.findById(user.getId()).get();

		for (UserShipping userShipping : localUser.getUserShippingList()) {
			if (userShipping.getId() == defaultUserShippingId) {
				userShipping.setUserShippingDefault(true);
				userShippingService.save(userShipping);
			} else {
				userShipping.setUserShippingDefault(false);
				userShippingService.save(userShipping);
			}
		}
		return userRepository.findById(user.getId()).get();
	}
}
