package com.diphlk.book.service;

import com.diphlk.book.dto.BookRequest;
import com.diphlk.book.dto.BookResponse;
import com.diphlk.book.dto.PageResponse;
import com.diphlk.book.mapper.BookMapper;
import com.diphlk.book.model.Book;
import com.diphlk.book.model.User;
import com.diphlk.book.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    public Integer saveBook(BookRequest bookRequest, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
    }

    public PageResponse<BookResponse> getAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );
        Page<Book> bookPage = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = bookPage.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isFirst(),
                bookPage.isLast()
        );
    }

    public PageResponse<BookResponse> getBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );
        Page<Book> bookPage = bookRepository.findAllByOwnerId(pageable, user.getId());
        List<BookResponse> bookResponses = bookPage.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                bookResponses,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isFirst(),
                bookPage.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );
        Page<Book> bookPage = bookRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BookResponse> bookResponses = bookPage.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                bookResponses,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isFirst(),
                bookPage.isLast()
        );
    }
}
