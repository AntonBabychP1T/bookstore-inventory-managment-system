package unillence.bookstoreims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unillence.bookstoreims.model.Book;

public interface BookRepository extends JpaRepository<Long, Book> {
}
