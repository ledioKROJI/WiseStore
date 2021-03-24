package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;

import be.ledio.model.security.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	
	Role findByName(String name);
}
