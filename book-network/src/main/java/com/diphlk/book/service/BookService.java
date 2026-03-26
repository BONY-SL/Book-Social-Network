package com.diphlk.book.service;

import com.diphlk.book.dto.BookRequest;
import com.diphlk.book.dto.BookResponse;
import com.diphlk.book.dto.BorrowedBookResponse;
import com.diphlk.book.dto.PageResponse;
import com.diphlk.book.exception.OperationNotPermittedException;
import com.diphlk.book.mapper.BookMapper;
import com.diphlk.book.model.Book;
import com.diphlk.book.model.BookTransactionHistory;
import com.diphlk.book.model.User;
import com.diphlk.book.repository.BookRepository;
import com.diphlk.book.repository.BookTransactionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

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

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );
        Page<BookTransactionHistory> bookTransactionHistoryPage = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponseList = bookTransactionHistoryPage.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                borrowedBookResponseList,
                bookTransactionHistoryPage.getNumber(),
                bookTransactionHistoryPage.getSize(),
                bookTransactionHistoryPage.getTotalElements(),
                bookTransactionHistoryPage.getTotalPages(),
                bookTransactionHistoryPage.isFirst(),
                bookTransactionHistoryPage.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );
        Page<BookTransactionHistory> bookTransactionHistoryPage = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponseList = bookTransactionHistoryPage.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                borrowedBookResponseList,
                bookTransactionHistoryPage.getNumber(),
                bookTransactionHistoryPage.getSize(),
                bookTransactionHistoryPage.getTotalElements(),
                bookTransactionHistoryPage.getTotalPages(),
                bookTransactionHistoryPage.isFirst(),
                bookTransactionHistoryPage.isLast()
        );
    }

    public Integer updateBookShareableStatus(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can only update the shareable status of your own books");
        }
        book.setShareable(!book.isShareable());
        return bookRepository.save(book).getId();
    }

    public Integer updateArchiveBookStatus(Integer bookId, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can only update the archive status of your own books");
        }
        book.setArchived(!book.isArchived());
        return bookRepository.save(book).getId();
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("This book is not available for borrowing");
        }
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory =
                BookTransactionHistory.builder()
                        .borrower(user)
                        .book(book)
                        .returned(false)
                        .returnApproved(false)
                        .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("This book is not available for Returning");
        }
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot Return your own book");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndBorrowerIdAndReturnedFalseAndReturnApprovedFalse(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No borrowing record found for the book with the ID :: " + bookId));
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnedBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        if (!book.isShareable() || book.isArchived()) {
            throw new OperationNotPermittedException("This book is not available for Returning approval");
        }
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can only approve the return of your own books");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndBookOwnerId(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No borrowing record found for the book with the ID :: " + bookId));
        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCover(Integer bookId, MultipartFile coverImage, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You can only upload cover image for your own books");
        }
        var bookCover = fileStorageService.storeFile(coverImage, user.getId());
        book.setBookCoverUrl(bookCover);
        bookRepository.save(book);
    }
}
