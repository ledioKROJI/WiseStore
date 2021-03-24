package be.ledio;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import be.ledio.model.User;
import be.ledio.model.security.Role;
import be.ledio.model.security.UserRole;
import be.ledio.service.UserService;
import be.ledio.utility.SecurityUtility;

@SpringBootApplication
public class WisestoreApplication implements CommandLineRunner {
	
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(WisestoreApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		User user1 = new User();
		user1.setFirstName("Arsène");
		user1.setLastName("Lupin");
		user1.setUserName("user");
		user1.setPassword(SecurityUtility.passwordEncoder().encode("user"));
		user1.setEmail("leddiode94@gmail.com");
		Set<UserRole> userRoles = new HashSet<>();
		Role role1= new Role();
		role1.setRoleId(1);
		role1.setName("ROLE_USER");
		userRoles.add(new UserRole(user1, role1));
		
		userService.createUser(user1, userRoles);
	}
}
