package unillence.bookstoreims.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unillence.bookstoreims.bookstore.AddBookRequest;
import unillence.bookstoreims.bookstore.BookMessage;
import unillence.bookstoreims.bookstore.DeleteBookRequest;
import unillence.bookstoreims.bookstore.DeleteBookResponse;
import unillence.bookstoreims.bookstore.GetBookRequest;
import unillence.bookstoreims.bookstore.OperationBookResponse;
import unillence.bookstoreims.bookstore.UpdateBookRequest;
import unillence.bookstoreims.mapper.BookMapper;
import unillence.bookstoreims.model.Book;
import unillence.bookstoreims.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplUnitTest {
    private static final String VALID_ID = "1";
    private static final String NOT_VALID_ID = "-1";
    private static final Long NOT_VALID_LONG_ID = -1L;
    private static final String TITLE = "Valid title";
    private static final String AUTHOR = "Valid author";
    private static final String ISBN = "1234567890";
    private static final int QUANTITY = 10;
    private static final String DESCRIPTION = "Sample description";
    private static final String COVER_IMAGE = "https://example.com/cover.jpg";

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private StreamObserver<OperationBookResponse> responseObserver;
    @Mock
    private StreamObserver<BookMessage> responseObserverGetBook;
    @Mock
    private StreamObserver<DeleteBookResponse> deleteResponseObserver;

    @Test
    @DisplayName("addBook() should return BookResponse Message")
    public void addBook_WithValidRequest_ShouldReturnMessageBook() {
        AddBookRequest request = createValidAddBookRequest();
        Book book = createBookWithAllFields();
        when(bookMapper.toModel(any())).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toAddResponse(any()))
                .thenReturn(OperationBookResponse.newBuilder().setSuccess(true).build());

        bookService.addBook(request, responseObserver);

        verify(bookMapper).toModel(any());
        verify(bookRepository).save(any(Book.class));
        verify(responseObserver).onNext(any());
        verify(responseObserver).onCompleted();
    }

    @Test
    @DisplayName("getBook() should return Book Message")
    public void getBook_WithValidRequest_ShouldReturnBook() {
        GetBookRequest request = GetBookRequest.newBuilder().setId(VALID_ID).build();
        Book book = createBookWithAllFields();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.fromModelToMessage(book)).thenReturn(BookMessage.newBuilder().build());

        bookService.getBook(request, responseObserverGetBook);

        verify(bookRepository).findById(anyLong());
        verify(bookMapper).fromModelToMessage(book);
        verify(responseObserverGetBook).onNext(any());
        verify(responseObserverGetBook).onCompleted();
    }

    @Test
    @DisplayName("getBook() should handle EntityNotFoundException")
    public void getBook_WithInvalidRequest_ShouldHandleEntityNotFoundException() {
        GetBookRequest request = GetBookRequest.newBuilder().setId(NOT_VALID_ID).build();
        when(bookRepository.findById(NOT_VALID_LONG_ID)).thenReturn(Optional.empty());

        bookService.getBook(request, responseObserverGetBook);

        verify(bookRepository).findById(NOT_VALID_LONG_ID);
        verify(responseObserverGetBook).onError(any(StatusRuntimeException.class));
    }

    @Test
    @DisplayName("updateBook() should update and return BookResponse Message")
    public void updateBook_WithValidRequest_ShouldUpdateAndReturnBook() {
        UpdateBookRequest request = createValidUpdateBookRequest();
        Book existingBook = createBookWithAllFields();
        when(bookMapper.updateBook(request)).thenReturn(existingBook);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toAddResponse(existingBook))
                .thenReturn(OperationBookResponse.newBuilder().setSuccess(true).build());

        bookService.updateBook(request, responseObserver);

        verify(bookMapper).updateBook(any());
        verify(bookRepository).save(any(Book.class));
        verify(responseObserver).onNext(any());
        verify(responseObserver).onCompleted();
    }

    @Test
    @DisplayName("deleteBook() should delete a book and return success response")
    void deleteBook_WithValidRequest_ShouldDeleteBook() {
        DeleteBookRequest request = DeleteBookRequest.newBuilder().setId(VALID_ID).build();
        Book book = createBookWithAllFields();
        book.setId(Long.parseLong(VALID_ID));
        when(bookRepository.findById(Long.parseLong(VALID_ID)))
                .thenReturn(Optional.of(book));

        bookService.deleteBook(request, deleteResponseObserver);

        verify(bookRepository).findById(Long.parseLong(VALID_ID));
        verify(bookRepository).delete(book);
        verify(deleteResponseObserver).onNext(any(DeleteBookResponse.class));
        verify(deleteResponseObserver).onCompleted();
    }

    @Test
    @DisplayName("deleteBook() should handle EntityNotFoundException")
    void deleteBook_WithInvalidRequest_ShouldHandleEntityNotFoundException() {
        DeleteBookRequest request = DeleteBookRequest.newBuilder().setId(NOT_VALID_ID).build();
        when(bookRepository.findById(Long.parseLong(NOT_VALID_ID))).thenReturn(Optional.empty());

        bookService.deleteBook(request, deleteResponseObserver);

        verify(bookRepository).findById(Long.parseLong(NOT_VALID_ID));
        verify(deleteResponseObserver).onError(any(StatusRuntimeException.class));
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

    private Book createBookWithAllFields() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle(TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(ISBN);
        book.setQuantity(QUANTITY);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);
        return book;
    }



}

