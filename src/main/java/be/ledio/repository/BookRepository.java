package be.ledio.repository;

import org.springframework.data.repository.CrudRepository;

import be.ledio.model.Book;

public interface BookRepository extends CrudRepository<Book, Long>{
	
}
