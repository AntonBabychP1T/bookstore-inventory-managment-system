package unillence.bookstoreims.service;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.junit.jupiter.CitrusExtension;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.annotations.CitrusResource;

import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import unillence.bookstoreims.bookstore.AddBookRequest;
import unillence.bookstoreims.bookstore.BookMessage;
import unillence.bookstoreims.bookstore.BookServiceGrpc;
import unillence.bookstoreims.bookstore.DeleteBookRequest;
import unillence.bookstoreims.bookstore.DeleteBookResponse;
import unillence.bookstoreims.bookstore.GetBookRequest;
import unillence.bookstoreims.bookstore.ListBooksRequest;
import unillence.bookstoreims.bookstore.ListBooksResponse;
import unillence.bookstoreims.bookstore.OperationBookResponse;
import unillence.bookstoreims.bookstore.UpdateBookRequest;
import unillence.bookstoreims.config.CustomPostgreSqlContainer;

@Testcontainers
@ExtendWith(CitrusExtension.class)
@SpringBootTest
public class BookServiceImplIntegrationTest {
    private static final String VALID_ID = "1";
    private static final String TITLE = "Valid Title";
    private static final String AUTHOR = "Valid Author";
    private static final String ISBN = "12345678";
    private static final int QUANTITY = 2;
    private static final String DESCRIPTION = "description";
    private static final String COVER_IMAGE = "https://example.com/cover.jpg";
    private static final CustomPostgreSqlContainer postgreSqlContainer
            = CustomPostgreSqlContainer.getInstance();

    @Autowired
    private BookServiceGrpc.BookServiceBlockingStub client;

    @Autowired
    private DataSource dataSource;

    static {
        postgreSqlContainer.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }

    @BeforeEach
    void setUp(@CitrusResource TestRunner runner) {
        runner.sql(action -> action.dataSource(dataSource)
                .sqlResource("classpath:database/delete-all-from-books.sql"));
    }

    @Test
    @CitrusTest
    @DisplayName("Verify addBook() method works")
    public void addBook_ValidRequest_ValidRespondAndRecordInDb(@CitrusResource TestRunner runner) {
        AddBookRequest request = createValidAddBookRequest();
        OperationBookResponse response = client.addBook(request);
        Assertions.assertTrue(response.getSuccess());
        Assertions.assertEquals("Book added successfully", response.getMessage());
        Assertions.assertNotNull(response.getBook());
        Assertions.assertEquals(TITLE, response.getBook().getTitle());
        Assertions.assertEquals(AUTHOR, response.getBook().getAuthor());

        runner.query(sqlAction -> sqlAction.dataSource(dataSource)
                .statement("SELECT COUNT(*) AS book_count FROM books WHERE isbn = '" + ISBN + "'")
                .validate("book_count", "1"));
    }

    @Test
    @CitrusTest
    @DisplayName("Verify updateBook() method works")
    public void testUpdateBook(@CitrusResource TestRunner runner) {
        runner.sql(action -> action.dataSource(dataSource)
                .sqlResource("classpath:database/add-default-book.sql"));

        UpdateBookRequest updateRequest = createValidUpdateBookRequest();
        OperationBookResponse updateResponse = client.updateBook(updateRequest);

        Assertions.assertTrue(updateResponse.getSuccess());
        Assertions.assertEquals("Book added successfully", updateResponse.getMessage());
        Assertions.assertNotNull(updateResponse.getBook());
        Assertions.assertEquals(TITLE, updateResponse.getBook().getTitle());
        Assertions.assertEquals(AUTHOR, updateResponse.getBook().getAuthor());
        Assertions.assertEquals(QUANTITY, updateResponse.getBook().getQuantity());
        runner.query(action -> action.dataSource(dataSource)
                .statement("SELECT COUNT(*) AS book_count FROM books WHERE isbn = '" + ISBN
                        + "' AND quantity = " + QUANTITY)
                .validate("book_count", "1"));
    }

    @CitrusTest
    @Test
    @DisplayName("Verify listBooks() returns correct data and data in DB is as expected")
    public void testListBooks(@CitrusResource TestRunner runner) {
        runner.sql(action -> action.dataSource(dataSource)
                .sqlResource("classpath:database/add-three-books.sql"));

        ListBooksRequest request = ListBooksRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        ListBooksResponse response = client.listBooks(request);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.getBooksList().isEmpty());
        Assertions.assertEquals(3, response.getBooksList().size());

        runner.query(action -> action.dataSource(dataSource)
                .statement("SELECT COUNT(*) AS book_count FROM books")
                .validate("book_count", "3"));

        runner.query(action -> action.dataSource(dataSource)
                .statement("SELECT title, author FROM books WHERE isbn = 'ValidISBN1'")
                .validate("title", "Valid Title1")
                .validate("author", "Valid Author1"));

    }

    @Test
    @CitrusTest
    @DisplayName("deleteBook() should handle EntityNotFoundException")
    public void deleteBook_WithInvalidRequest_ShouldHandleEntityNotFoundException(@CitrusResource TestRunner runner) {
        runner.sql(action -> action.dataSource(dataSource)
                .sqlResource("classpath:database/add-default-book.sql"));

        DeleteBookRequest deleteRequest = DeleteBookRequest.newBuilder().setId(VALID_ID).build();
        DeleteBookResponse deleteResponse = client.deleteBook(deleteRequest);

        Assertions.assertTrue(deleteResponse.getSuccess());
        Assertions.assertEquals("Book with id: " + VALID_ID + " deleted", deleteResponse.getMessage());

        runner.query(action -> action.dataSource(dataSource)
                .statement("SELECT deleted FROM books WHERE id = '" + VALID_ID + "'")
                .validate("deleted", "true"));
    }

    @Test
    @CitrusTest
    @DisplayName("getBook() should retrieve the correct book")
    public void getBook_RetrieveCorrectBook(@CitrusResource TestRunner runner) {
        runner.sql(action -> action.dataSource(dataSource)
                .sqlResource("classpath:database/add-default-book.sql"));

        GetBookRequest request = GetBookRequest.newBuilder().setId(VALID_ID).build();
        BookMessage response = client.getBook(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(VALID_ID, response.getId());
        Assertions.assertEquals(TITLE, response.getTitle());
        Assertions.assertEquals(AUTHOR, response.getAuthor());

        runner.query(action -> action.dataSource(dataSource)
                .statement("SELECT COUNT(*) AS book_count FROM books WHERE id = '" + VALID_ID + "'")
                .validate("book_count", "1"));
    }

    private AddBookRequest createValidAddBookRequest() {
        return AddBookRequest.newBuilder()
                .setTitle(TITLE)
                .setAuthor(AUTHOR)
                .setIsbn(ISBN)
                .setQuantity(QUANTITY)
                .setDescription(DESCRIPTION)
                .setCoverImage(COVER_IMAGE)
                .build();
    }

    private UpdateBookRequest createValidUpdateBookRequest() {
        return UpdateBookRequest.newBuilder()
                .setId(VALID_ID)
                .setTitle(TITLE)
                .setAuthor(AUTHOR)
                .setIsbn(ISBN)
                .setQuantity(QUANTITY)
                .setDescription(DESCRIPTION)
                .setCoverImage(COVER_IMAGE)
                .build();
    }
}
