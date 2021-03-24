package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import be.ledio.model.User;

@Service
public interface UserRepository extends CrudRepository<User, Long> {
	//	Here Spring Boot knows that we have a model named User (declared in the CrudRepo class
	//	Spring boot will automatically match the username field in User object to return the object
	//	This is powerful!
	User findByUserName(String userName);
	
	User findByEmail(String email);
}
