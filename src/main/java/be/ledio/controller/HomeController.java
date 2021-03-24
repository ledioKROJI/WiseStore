package be.ledio.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import be.ledio.model.Book;
import be.ledio.model.User;
import be.ledio.model.UserBilling;
import be.ledio.model.UserPayment;
import be.ledio.model.UserShipping;
import be.ledio.model.security.PasswordResetToken;
import be.ledio.model.security.Role;
import be.ledio.model.security.UserRole;
import be.ledio.service.BookService;
import be.ledio.service.UserBillingService;
import be.ledio.service.UserPaymentService;
import be.ledio.service.UserService;
import be.ledio.service.UserShippingService;
import be.ledio.service.impl.UserSecurityService;
import be.ledio.utility.EUConstants;
import be.ledio.utility.MailConstructor;
import be.ledio.utility.SecurityUtility;

@Controller
public class HomeController {
	// The mapping must be specified in the SecurityConfig class
	// Modifications to those classes need a reboot of the app

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	// This is the top interface of the Java Mail API
	private JavaMailSender mailSender;

	@Autowired
	// Custom utility to construct a mail
	private MailConstructor mailConstructor;

	@Autowired
	private UserPaymentService userPaymentService;

	@Autowired
	private UserBillingService userBillingService;

	@Autowired
	private UserShippingService userShippingService;

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	// Only trigger if there is a POST request
	@RequestMapping(value = "/create-new-account", method = RequestMethod.POST) // 1 POST
	public String newUserPost(HttpServletRequest request, @ModelAttribute("email") String userEmail,
			@ModelAttribute("username") String username, Model model) throws Exception {
		// Reactivate the create new account tab !
		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", userEmail);
		model.addAttribute("username", username);

		// Simple check
		if (userService.findByUsername(username) != null) {
			model.addAttribute("usernameExists", true);
			model.addAttribute("newAccountClassActive", true);
			return "my-account";
		}

		// Simple check
		if (userService.findByEmail(userEmail) != null) {
			model.addAttribute("emailExists", true);
			model.addAttribute("newAccountClassActive", true);
			return "my-account";
		}

		// Create a User entity with the inputs of the real user
		User user = new User();
		user.setUserName(username);
		user.setEmail(userEmail);

		// This generate a random pw to be send by email and encrypt it
		String password = SecurityUtility.randomPassword();
		// Is the password only encrypted for the DB? IDK
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		// Create a new role and add it to an UserRole object
		// Create also UserRole Entity and map the role to the user
		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));
		userService.createUser(user, userRoles);

		// This is a Universal UID - Universally Unique IDentifier
		String token = UUID.randomUUID().toString();
		// It just create a token associated, store it in the DB but don't return it !
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
				password);
		mailSender.send(email);

		model.addAttribute("newAccountClassActive", true);
		model.addAttribute("emailSent", "true");
		return "my-account";
	}

	// Here the user has jsut created a new account, the from box shows a message
	// that says "go check email"
	// In the email we will have a link to this path with a token and then the
	// method process the token
	// This method redirect to a new page that is for logged user and shows an
	// "Edit" tab where we can change informations (email, pass, name, ...)
	@RequestMapping(value = "/create-new-account", method = RequestMethod.GET) // 2 GET
	// Where does the "token" param goes in the template??? IDK
	public String newUserGet(Model model, @RequestParam("token") String token) {
		// PRT is a simple Entity ; Goes directly to the Repository to retrieve a
		// PasswordResetToken entity in the DB (with all its other fields...)
		PasswordResetToken passToken = userService.getPasswordResetToken(token);

		if (passToken == null) {
			String message = "Invalid Token.";
			model.addAttribute("message", message);
			// We must define a "badRequest page to be redirected to...
			return "redirect:/badRequest";
		}

		// One of the fields of PasswordResetToken...
		User user = passToken.getUser();
		String username = user.getUsername();

		// From the "token" param we've got a PasswordResetToken
		// from this PRT we've got an User object
		// from the User we've got a UserDetails object
		// Then we can use this UserDetails for authentication
		// 59 to 64 make sure that the current login session is for the right user...
		// It set the current login session to that user...
		UserDetails userDetails = userSecurityService.loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		// I think it set the authentication to the application scope... Not sure...
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// model.addAttribute("newAccountClassActive", true);
		// Where goes the "classActiveEdit" attribute??? IDK
		model.addAttribute("user", user);
		model.addAttribute("classActiveEdit", true);
		return "my-profile";
	}

	// This method is set to be the fallback in SEcurityConfig class
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("loginClassActive", true);
		return "my-account";
	}

	@RequestMapping("/forget-password")
	public String forgetPassword(HttpServletRequest request, @ModelAttribute("email") String email, Model model) {

		model.addAttribute("classActiveForgetPassword", true);

		User user = userService.findByEmail(email);

		if (user == null) {
			model.addAttribute("emailNotExist", true);
			model.addAttribute("forgetPasswordClassActive", true);
			return "my-account";
		}

		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		userService.save(user);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
				password);

		mailSender.send(newEmail);

		model.addAttribute("forgetPasswordEmailSent", true);
		model.addAttribute("forgetPasswordClassActive", true);

		return "my-account";
	}

	@RequestMapping("/book-shelf")
	public String getBookShelf(Model model) {
		List<Book> bookList = bookService.findAll();
		model.addAttribute("bookList", bookList);
		return "book-shelf";
	}

	@RequestMapping("/book-detail")
	public String bookDetail(@RequestParam("id") Long id, Model model) { // Principal principal
//		This allow us to get information of the current user (we can also user the authentication class to do this)
//		if (principal != null) {
//			String username = principal.getName();
//			User user = userService.findByUsername(username);
//			model.addAttribute("user", user);
//		}

		Optional<Book> optionalBook = bookService.findById(id);
		Book book = new Book();
		book = optionalBook.get();
		model.addAttribute("book", book);

		return "book-detail";
	}

	@RequestMapping("/my-profil")
	public String profile(Model model, Principal principal) {

		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("classActiveEdit", true);

		return "my-profile";
	}

	@RequestMapping("/list-of-credit-card")
	public String listOfCreditCard(Model model, Principal principal) {

		// This generate the list in the billing, shipping and orders tabs
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());

		// Various If statements values in the Thymeleaf page
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);

		return "my-profile";
	}

	@RequestMapping(value = "/add-new-credit-card", method = RequestMethod.GET)
	public String addNewCreditCard(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		// It prevent from having error when running the credit card form without real
		// user's data...
		UserPayment userPayment = new UserPayment();
		model.addAttribute("userPayment", userPayment);
		UserBilling userBilling = new UserBilling();
		model.addAttribute("userBilling", userBilling);

		// This generate the select options for the Country field
		// Currently it is with US codes but we can change it...
		List<String> countryList = EUConstants.listOfEUCountryCode;
		Collections.sort(countryList);
		model.addAttribute("countryList", countryList);

		// Various If statements values in the Thymeleaf page
		model.addAttribute("addNewCreditCard", true);
		model.addAttribute("classActiveBilling", true);

		return "my-profile";
	}

	@RequestMapping(value = "/add-new-credit-card", method = RequestMethod.POST)
	public String addNewCreditCardPost(Model model, @ModelAttribute("userPayment") UserPayment userPayment,
			@ModelAttribute("userBilling") UserBilling userBilling, Principal principal) {

		// Complicated way to get the id firstly assigned to the UserBilling DB from the
		// UserPayment object since UserBilling is a nested object of UserPayment...
		if (userPayment.getId() != null) {
			userBilling.setId(userPaymentService.findById(userPayment.getId()).get().getUserBilling().getId());
			System.out.println("User billing from add-new-credit-card : " + userBilling.getId());
		}

		User user = userService.findByUsername(principal.getName());

		userService.updateUserBilling(userBilling, userPayment, user);

		// It just pass the filled object to the new form... After having them persisted
		// !!! We should add here the return value form the repository service and not
		// the model !!!
		model.addAttribute("userPayment", userPayment);
		model.addAttribute("userBilling", userBilling);

		// This generate the select options for the Country field
		// Currently it is with US codes but we can change it...
		List<String> countryList = EUConstants.listOfEUCountryCode;
		Collections.sort(countryList);
		model.addAttribute("countryList", countryList);

		model.addAttribute("classActiveBilling", true);
		model.addAttribute("addNewCreditCard", true);
		model.addAttribute("updateUserPaymentInfo", true);
		model.addAttribute("user", user);
		return "my-profile";
	}

	@RequestMapping("/update-credit-card")
	public String updateCreditCard(@ModelAttribute("id") Long creditCardId, Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());
		Optional<UserPayment> optionalUserPayment = userPaymentService.findById(creditCardId);
		UserPayment userPayment = optionalUserPayment.get();

		Optional<UserBilling> optionalUserBilling = userBillingService.findById(userPayment.getUserBilling().getId());
		UserBilling userBilling = optionalUserBilling.get();

		if (user.getId() != userPayment.getUser().getId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			model.addAttribute("userPayment", userPayment);
			model.addAttribute("userBilling", userBilling);

			List<String> countryList = EUConstants.listOfEUCountryCode;
			Collections.sort(countryList);
			model.addAttribute("countryList", countryList);

			model.addAttribute("classActiveBilling", true);
			model.addAttribute("addNewCreditCard", true);
			return "my-profile";
		}
	}

	@RequestMapping("/remove-card")
	public String removeCard(Model model, Principal principal, @ModelAttribute("id") Long userPaymentId) {
		userPaymentService.deleteById(userPaymentId);
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("CardDeleted", true);
		model.addAttribute("classActiveBilling", true);
		return "my-profile";
	}

	@RequestMapping(value = "/set-default-card", method = RequestMethod.POST)
	public String setDefaultCreditCard(Model model, Principal principal,
			@ModelAttribute("defaultUserPaymentId") Long defaultUserPaymentId) {
		User user = userService.findByUsername(principal.getName());
		user = userService.setUserDefaultCreditCard(defaultUserPaymentId, user);

		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("DefaultCardChanged", true);
		model.addAttribute("classActiveBilling", true);
		return "my-profile";
	}

	@RequestMapping("/list-shipping-addresses")
	public String listOfShippingAdresses(Model model, Principal principal) {

		// This generate the list in the billing, shipping and orders tabs
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userShippingList", user.getUserShippingList());

		// Various If statements values in the Thymeleaf page
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		return "my-profile";
	}

	@RequestMapping(value = "/add-shipping-address", method = RequestMethod.GET)
	public String addNewShippingAddress(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		// It prevent from having error when running the credit card form without real
		// user's data...
		UserShipping userShipping = new UserShipping();
		model.addAttribute("userShipping", userShipping);

		// This generate the select options for the Country field
		// Currently it is with US codes but we can change it...
		List<String> countryList = EUConstants.listOfEUCountryCode;
		Collections.sort(countryList);
		model.addAttribute("countryList", countryList);

		// Various If statements values in the Thymeleaf page
		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("classActiveShipping", true);
		return "my-profile";
	}

	@RequestMapping(value = "/add-shipping-address", method = RequestMethod.POST)
	public String addNewShippingAddressPost(Model model, Principal principal,
			@ModelAttribute("userShipping") UserShipping userShipping) {
		User user = userService.findByUsername(principal.getName());
		userService.updateUserShipping(userShipping, user);

		List<String> countryList = EUConstants.listOfEUCountryCode;
		Collections.sort(countryList);
		model.addAttribute("countryList", countryList);

		model.addAttribute("userShipping", userShipping);
		model.addAttribute("user", user);

		model.addAttribute("classActiveShipping", true);
		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("updateUserShippingInfo", true);
		return "my-profile";
	}

	@RequestMapping("/update-shipping-address")
	public String updateShippingAddress(Model model, @ModelAttribute("id") Long userShippingId, Principal principal) {

		User user = userService.findByUsername(principal.getName());
		UserShipping userShipping = userShippingService.findById(userShippingId).get();

		if (user.getId() != userShipping.getUser().getId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			model.addAttribute("userShipping", userShipping);

			List<String> countryList = EUConstants.listOfEUCountryCode;
			Collections.sort(countryList);
			model.addAttribute("countryList", countryList);

			model.addAttribute("classActiveShipping", true);
			model.addAttribute("addNewShippingAddress", true);
			return "my-profile";
		}
	}

	@RequestMapping("/remove-shipping-address")
	public String removeShippingAddress(Model model, Principal principal, @ModelAttribute("id") Long userShippingId) {
		userShippingService.remove(userShippingId);
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);
		model.addAttribute("userShippingList", user.getUserShippingList());

		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("ShippingDeleted", true);
		return "my-profile";
	}

	@RequestMapping(value = "/set-default-shipping-address", method = RequestMethod.POST)
	public String setDefaultShippingAddress(Model model, Principal principal,
			@ModelAttribute("defaultUserShippingId") Long defaultUserShippingId) {
		User user = userService.findByUsername(principal.getName());
		user = userService.setUserDefaultShippingAddress(defaultUserShippingId, user);

		model.addAttribute("user", user);
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("DefaultShippingAddressChanged", true);
		return "my-profile";
	}

	@RequestMapping("/test")
	public String test(Model model, Principal principal) {
		return "checkout";

	}

	@RequestMapping(value = "/update-user-info", method = RequestMethod.POST)
	public String updateUserInfo(@ModelAttribute("user") User user, @ModelAttribute("newPassword") String newPassword,
			Model model) throws Exception {
		User currentUser = userService.findById(user.getId());

		// Check if the user variable is present in the page
		if (currentUser == null) {
			throw new Exception("User not found");
		}

		// Check if the email already exists
		if (userService.findByEmail(user.getEmail()) != null) {
			// Get the user by the email and check if he has the same email as the user got
			// by Id
			if (userService.findByEmail(user.getEmail()).getId() != currentUser.getId()) {
				model.addAttribute("emailExists", true);
				return "myProfile";
			}
		}

		/* check username already exists */
		if (userService.findByUsername(user.getUsername()) != null) {
			if (userService.findByUsername(user.getUsername()).getId() != currentUser.getId()) {
				model.addAttribute("usernameExists", true);
				return "myProfile";
			}
		}

		// If the new password is not unchanged (like null, empty, "")
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
			// Initialize the encryption mode
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			// Get the current password (send by email)
			String dbPassword = currentUser.getPassword();
			// Check if the password (copied and pasted from the email to the form) is the
			// same as the password in the database after decrypting it
			if (passwordEncoder.matches(user.getPassword(), dbPassword)) {
				// Set the new password as the default password
				currentUser.setPassword(passwordEncoder.encode(newPassword));
			} else {
				model.addAttribute("incorrectPassword", true);
				return "myProfile";
			}
		}

		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUserName(user.getUsername());
		currentUser.setEmail(user.getEmail());

		userService.save(currentUser);

		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", currentUser);
		model.addAttribute("classActiveEdit", true);

		// It only give to the user with the new password the ability to say connected
		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "my-profile";
	}
}
