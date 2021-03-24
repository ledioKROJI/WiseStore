package be.ledio.utility;

import java.util.Locale;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import be.ledio.model.Order;
import be.ledio.model.User;

@Component
public class MailConstructor {
	@Autowired
	private Environment env;

	@Autowired
	private TemplateEngine templateEngine;

	public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user,
			String password) {
		// This wil trigger the mapping "/create-new-user" 2 ; It wil directly login the
		// user to his account and let him edit his informations
		String url = contextPath + "/create-new-account?token=" + token;
		String message = "\nPlease click on this link to verify your email and edit your personal information. Your password is: \n"
				+ password;
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		email.setSubject("WiseStore - New User");
		email.setText(url + message);
		// This goes to the "application.properties" file and get all the email
		// informations
		email.setFrom(env.getProperty("support.email"));
		return email;

	}

	public MimeMessagePreparator constructOrderConfirmationEmail(User user, Order order, Locale locale) {
		Context context = new Context();
		// set some variable to the Thymeleaf HTML template that is used as a
		// Confirmation Email
		context.setVariable("order", order);
		context.setVariable("user", user);
		context.setVariable("cartItemList", order.getCartItemList());
		String text = templateEngine.process("order-confirmation-mail", context);

		// This process the email itself
		MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
				email.setTo(user.getEmail());
				email.setSubject("Order Confirmation - " + order.getId());
				email.setText(text, true);
				email.setFrom(new InternetAddress("ray.deng83@gmail.com"));
			}
		};

		return messagePreparator;
	}
}
