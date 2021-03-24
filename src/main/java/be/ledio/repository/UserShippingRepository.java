package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import be.ledio.model.UserShipping;

@Service
public interface UserShippingRepository extends CrudRepository<UserShipping, Long>{

}
