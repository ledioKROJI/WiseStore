package be.ledio.repository;

import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import be.ledio.model.User;
import be.ledio.model.security.PasswordResetToken;

//	The JpaRepository automatically map the BD to the object through method/field names
//	For this reason we must specify the object to be persisted and the type of its ID field
//	The JpaRepository implements the CrudRepository too !
//	Spring create an implementation of this interface and all the nested interfaces in the JpaRepo
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	// Find an instance of PRT in the DB by the token automatically !!!
	PasswordResetToken findByToken(String token);

	PasswordResetToken findByUser(User user);

	//	I think that here must be an @Query annotation but I'm not sure...
	Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

	@Modifying
	// Here it delete PRT with this JPQL statement ; "?1" represent the first
	// parameter of the annotated method
	@Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
	void deleteAllExpiredSince(Date now);

}