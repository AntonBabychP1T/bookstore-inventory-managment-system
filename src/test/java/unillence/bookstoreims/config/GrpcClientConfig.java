package unillence.bookstoreims.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unillence.bookstoreims.bookstore.BookServiceGrpc;

@Configuration
public class GrpcClientConfig {
    //@Value("${grpc.server.port}")
    private int grpcPort = 9090;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public BookServiceGrpc.BookServiceBlockingStub bookServiceStub(ManagedChannel channel) {
        return BookServiceGrpc.newBlockingStub(channel);
    }
}
