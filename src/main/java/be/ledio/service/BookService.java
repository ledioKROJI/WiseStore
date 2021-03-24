package be.ledio.service;

import java.util.List;
import java.util.Optional;

import be.ledio.model.Book;

public interface BookService {

	public Book save(Book book);

	public List<Book> findAll();
	
	public Optional<Book> findById(long id);
}
