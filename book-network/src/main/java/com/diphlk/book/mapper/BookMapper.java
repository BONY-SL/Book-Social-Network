package com.diphlk.book.mapper;

import com.diphlk.book.dto.BookRequest;
import com.diphlk.book.dto.BookResponse;
import com.diphlk.book.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookMapper {
    public Book toBook(BookRequest bookRequest) {
        return Book.builder()
                .id(bookRequest.id())
                .title(bookRequest.title())
                .author(bookRequest.authorName())
                .synopsis(bookRequest.synopsis())
                .archived(false)
                .isbn(bookRequest.isbn())
                .shareable(bookRequest.shareable())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthor())
                .synopsis(book.getSynopsis())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .ownerName(book.getOwner().fullName())
                .bookCover(null)
                .archived(book.isArchived())
                .rate(book.getRate())
                .build();
    }
}
