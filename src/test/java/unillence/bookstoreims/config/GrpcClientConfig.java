package unillence.bookstoreims.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unillence.bookstoreims.bookstore.BookServiceGrpc;

@Configuration
public class GrpcClientConfig {
    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    @Bean
    public BookServiceGrpc.BookServiceBlockingStub createClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();
        return BookServiceGrpc.newBlockingStub(channel);
    }
}
