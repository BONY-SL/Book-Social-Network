package com.diphlk.book.repository;

import com.diphlk.book.model.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query(
            """
            SELECT borrowing
            FROM BookTransactionHistory borrowing
            WHERE borrowing.borrower.id = :id
            """
    )
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer id);

    @Query(
            """
            SELECT borrowing
            FROM BookTransactionHistory borrowing
            WHERE borrowing.book.owner.id = :id
            AND borrowing.returned = true
            """
    )
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer id);


    @Query(
            """
            SELECT (COUNT (*) > 0) AS isAlreadyBorrowed
            FROM BookTransactionHistory borrowing
            WHERE borrowing.book.id = :bookId
            AND borrowing.borrower.id = :userId
            AND borrowing.returnApproved = false
            """
    )
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    Optional<BookTransactionHistory> findByBookIdAndBorrowerIdAndReturnedFalseAndReturnApprovedFalse(Integer bookId, Integer borrowerId);

    @Query(
            """
            SELECT borrowing
            FROM BookTransactionHistory borrowing
            WHERE borrowing.book.id = :bookId
            AND borrowing.book.owner.id = :userId
            AND borrowing.returned = true
            AND borrowing.returnApproved = false
            """
    )
    Optional<BookTransactionHistory> findByBookIdAndBookOwnerId(Integer bookId, Integer userId);
}
