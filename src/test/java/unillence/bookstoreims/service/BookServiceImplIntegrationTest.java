package unillence.bookstoreims.service;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.junit.jupiter.spring.CitrusSpringExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import unillence.bookstoreims.bookstore.AddBookRequest;
import unillence.bookstoreims.bookstore.BookMessage;
import unillence.bookstoreims.bookstore.BookServiceGrpc;
import unillence.bookstoreims.bookstore.GetBookRequest;
import unillence.bookstoreims.bookstore.OperationBookResponse;
import unillence.bookstoreims.config.CustomPostgreSqlContainer;
import unillence.bookstoreims.config.GrpcClientConfig;

import static com.consol.citrus.actions.EchoAction.Builder.echo;

@ContextConfiguration(classes = {GrpcClientConfig.class })
@Testcontainers
@ExtendWith(CitrusSpringExtension.class)
public class BookServiceImplIntegrationTest {
    private static final CustomPostgreSqlContainer postgreSQLContainer
            = CustomPostgreSqlContainer.getInstance();


    @Autowired
    private BookServiceGrpc.BookServiceBlockingStub bookServiceStub;

    @BeforeAll
    public static void setUp() {
        postgreSQLContainer.start();
    }

    //@Test
    @CitrusTest
    public void testGetBook(TestContext context) {

        echo("Test gRPC call to get a book");

        GetBookRequest request = GetBookRequest.newBuilder()
                .setId("1") // Встановіть реальний ідентифікатор книги
                .build();

        BookMessage response = bookServiceStub.getBook(request);

        org.junit.jupiter.api.Assertions.assertEquals("Expected Title", response.getTitle());
        org.junit.jupiter.api.Assertions.assertEquals("Expected Author", response.getAuthor());
    }

    //@Test
    @CitrusTest
    public void testAddBook(TestContext context) {

        AddBookRequest request = AddBookRequest.newBuilder()
                .setTitle("New Book")
                .setAuthor("Author")
                .setIsbn("ISBN-12345")
                .setQuantity(5)
                // інші поля...
                .build();

        OperationBookResponse response = bookServiceStub.addBook(request);

        // Перевірка, що відповідь успішна та містить очікувані дані
        org.junit.jupiter.api.Assertions.assertTrue(response.getSuccess());
        org.junit.jupiter.api.Assertions.assertEquals("Book added successfully", response.getMessage());
        // Можна також перевірити інші поля відповіді, наприклад дані книги, якщо вони повертаються
    }
}
