package unillence.bookstoreims.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import unillence.bookstoreims.bookstore.*;
import unillence.bookstoreims.mapper.BookMapper;
import unillence.bookstoreims.model.Book;
import unillence.bookstoreims.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<OperationBookResponse> responseObserver) {
        Book book = bookMapper.updateBook(request);

        responseObserver.onNext(bookMapper.toAddResponse(bookRepository.save(book)));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        try {
            Book book = getBookById(Long.valueOf(request.getId()));
            bookRepository.delete(book);
            DeleteBookResponse response = DeleteBookResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Book with id: " + request.getId() + " deleted")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listBooks(ListBooksRequest request, StreamObserver<ListBooksResponse> responseObserver) {
        Page<Book> booksPage = bookRepository.findAll(bookMapper.mapListBooksRequestToListBooksPageable(request));
        ListBooksResponse response = buildListBooksResponse(booksPage);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addBook(
            unillence.bookstoreims.bookstore.AddBookRequest request,
            StreamObserver<unillence.bookstoreims.bookstore.OperationBookResponse> responseObserver
    ) {
        OperationBookResponse response =
                bookMapper.toAddResponse(bookRepository.save(bookMapper.toModel(request)));
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBook(unillence.bookstoreims.bookstore.GetBookRequest request,
                        StreamObserver<unillence.bookstoreims.bookstore.Book> responseObserver) {
        try {
            Book book = getBookById(Long.valueOf(request.getId()));
            unillence.bookstoreims.bookstore.Book response = bookMapper.fromModelToMessage(book);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private ListBooksResponse buildListBooksResponse(Page<Book> booksPage) {
        ListBooksResponse.Builder responseBuilder = ListBooksResponse.newBuilder();
        for (Book book : booksPage) {
            responseBuilder.addBooks(bookMapper.fromModelToMessage(book));
        }
        return responseBuilder
                .setTotalPages(booksPage.getTotalPages())
                .setTotalElements((int) booksPage.getTotalElements())
                .build();
    }

    private Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book with id=" + id));
    }
}