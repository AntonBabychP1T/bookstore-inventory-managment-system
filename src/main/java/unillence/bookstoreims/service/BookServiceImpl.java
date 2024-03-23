package unillence.bookstoreims.service;

import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import unillence.bookstoreims.bookstore.*;
import unillence.bookstoreims.model.Book;
import unillence.bookstoreims.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {
    private final BookRepository bookRepository;

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        super.updateBook(request, responseObserver);
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        super.deleteBook(request, responseObserver);
    }

    @Override
    public void listBooks(ListBooksRequest request, StreamObserver<ListBooksResponse> responseObserver) {
        super.listBooks(request, responseObserver);
    }

    @Override
    public void addBook(
            unillence.bookstoreims.bookstore.AddBookRequest request,
            StreamObserver<unillence.bookstoreims.bookstore.AddBookResponse> responseObserver
    ) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setQuantity(request.getQuantity());

        bookRepository.save(book);
        AddBookResponse response = AddBookResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Book added successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBook(unillence.bookstoreims.bookstore.GetBookRequest request,
                        StreamObserver<unillence.bookstoreims.bookstore.Book> responseObserver) {
        Book book = getBookById(Long.valueOf(request.getId()));
        unillence.bookstoreims.bookstore.Book response = unillence.bookstoreims.bookstore.Book.newBuilder()
                .setId(String.valueOf(book.getId()))
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setQuantity(book.getQuantity())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        super.getBook(request, responseObserver);
    }

    private Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book with id=" + id));
    }
}