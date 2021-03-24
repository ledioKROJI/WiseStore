package be.ledio.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import be.ledio.service.impl.UserSecurityService;
import be.ledio.utility.SecurityUtility;

// Tell Spring that this is a Config class, when Spring init, it search for it
@Configuration
// This enable the Security framework of Spring Boot
@EnableWebSecurity
// Enable us to have fine-grainer user roles and privileges
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	// @Autowired
	// It get some parameters and environments variables from Spring
	private Environment env;

	@Autowired
	private UserSecurityService userSecurityService;

	private BCryptPasswordEncoder passwordEncoder() {
		return SecurityUtility.passwordEncoder();
	}

	// Here are contents that doesn't require security validation to be accessed
	// "/css/**" == from the css folder and beyond...
	private static final String[] PUBLIC_MATCHERS = { 
			
			"/",
			"/book-shelf", 
			"/book-detail",
			"/create-new-account",
			"/css/**", 
			"/font/**", 
			"/forget-password",
			"/image/**", 
			"/js/**", 
			"/login"};

	@Override
	// This method configure the HttpSecurity object
	protected void configure(HttpSecurity http) throws Exception {
		// This method allow any request which is in PUBLIC_MATCHERS
		// for the others request it require an authentication.
		http.authorizeRequests().
		/* antMatchers("/**"). */
				antMatchers(PUBLIC_MATCHERS).permitAll().anyRequest().authenticated();

		// Cross-site request forgery
		http.csrf().disable()
				// Cross-Origin Resource Sharing
				.cors().disable().
				// Here is a login form
				// Here Spring check the login and pwd if it success it redirect us to "/" with
				// the "authenticated" label and show us the "Logout" navbar because we are
				// authenticated otherwise it redirect us to the "/login" in case of wrong url
				// or "/login?error" in case of wrong login and pwd
				formLogin().failureUrl("/login?error").defaultSuccessUrl("/").loginPage("/login").permitAll().

				and()
				// Here it specifies where to beredirected and how when the user log out
				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout")
				.deleteCookies("remember-me").permitAll().and().rememberMe();
	}

	// @Autowired
	// It specifies an authentication manager
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// This check the password of the user and encode it
		auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
	}

}
