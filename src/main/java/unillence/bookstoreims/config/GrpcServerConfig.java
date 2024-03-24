package unillence.bookstoreims.config;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.context.annotation.Configuration;
import unillence.bookstoreims.bookstore.BookServiceGrpc;
import unillence.bookstoreims.mapper.BookMapper;
import unillence.bookstoreims.repository.BookRepository;
import unillence.bookstoreims.service.BookServiceImpl;

@RequiredArgsConstructor
@Configuration
public class GrpcServerConfig {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @GrpcService
    public BookServiceGrpc.BookServiceImplBase bookService() {
        return new BookServiceImpl(bookRepository, bookMapper);
    }
}
