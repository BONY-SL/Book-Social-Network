package com.diphlk.book.mapper;

import com.diphlk.book.dto.FeedBackRequest;
import com.diphlk.book.dto.FeedBackResponse;
import com.diphlk.book.model.Book;
import com.diphlk.book.model.FeedBack;
import org.springframework.stereotype.Service;

@Service
public class FeedBackMapper {

    public FeedBack toFeedBack(FeedBackRequest feedBackRequest, Book book) {
        return FeedBack.builder()
                .comment(feedBackRequest.comment())
                .note(feedBackRequest.note())
                .book(book)
                .build();
    }

    public FeedBackResponse toFeedBackResponse(FeedBack feedBack, Integer id) {
        return FeedBackResponse.builder()
                .comment(feedBack.getComment())
                .note(feedBack.getNote())
                .isMine(feedBack.getBook().getOwner().getId().equals(id))
                .build();
    }
}
