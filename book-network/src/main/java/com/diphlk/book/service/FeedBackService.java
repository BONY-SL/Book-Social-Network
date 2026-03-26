package com.diphlk.book.service;

import com.diphlk.book.dto.FeedBackRequest;
import com.diphlk.book.dto.FeedBackResponse;
import com.diphlk.book.dto.PageResponse;
import com.diphlk.book.exception.OperationNotPermittedException;
import com.diphlk.book.mapper.FeedBackMapper;
import com.diphlk.book.model.Book;
import com.diphlk.book.model.FeedBack;
import com.diphlk.book.model.User;
import com.diphlk.book.repository.BookRepository;
import com.diphlk.book.repository.FeedBackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final BookRepository bookRepository;
    private final FeedBackRepository feedBackRepository;
    private final FeedBackMapper feedBackMapper;
    public Integer saveFeedBack(FeedBackRequest feedBackRequest, Authentication connectedUser) {
        Book book = bookRepository.findById(feedBackRequest.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + feedBackRequest.bookId()));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("You Can not give feedback for this book");
        }
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You Can not give feedback for your own book");
        }
        FeedBack feedBack = feedBackMapper.toFeedBack(feedBackRequest,book);
        return feedBackRepository.save(feedBack).getId();
    }

    public PageResponse<FeedBackResponse> getAllFeedBacksByBookId(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) connectedUser.getPrincipal();
        Page<FeedBack> feedBackPage = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedBackResponse> feedBackResponseList = feedBackPage.stream()
                .map(feedBack -> feedBackMapper.toFeedBackResponse(feedBack, user.getId()))
                .toList();
        return new PageResponse<>(
                feedBackResponseList,
                feedBackPage.getNumber(),
                feedBackPage.getSize(),
                feedBackPage.getTotalElements(),
                feedBackPage.getTotalPages(),
                feedBackPage.isFirst(),
                feedBackPage.isLast()
        );
    }
}
