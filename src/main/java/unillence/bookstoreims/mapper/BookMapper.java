package unillence.bookstoreims.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import unillence.bookstoreims.bookstore.AddBookRequest;
import unillence.bookstoreims.bookstore.OperationBookResponse;
import unillence.bookstoreims.bookstore.UpdateBookRequest;
import unillence.bookstoreims.config.MapperConfig;
import unillence.bookstoreims.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    Book toModel(AddBookRequest request);

    unillence.bookstoreims.bookstore.Book fromModelToMessage(Book book);

    default OperationBookResponse toAddResponse(Book book) {
        return OperationBookResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Book added successfully")
                .setBook(fromModelToMessage(book))
                .build();
    }

    Book updateBook(UpdateBookRequest request);

    default Pageable mapListBooksRequestToListBooksPageable(unillence.bookstoreims.bookstore.ListBooksRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortOrder()), request.getSortField());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

}
