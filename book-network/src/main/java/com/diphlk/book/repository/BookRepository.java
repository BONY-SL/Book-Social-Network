package com.diphlk.book.repository;

import com.diphlk.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query(
            """
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.owner.id != :id
            """
    )
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer id);

    @Query(
            """
            SELECT book
            FROM Book book
            WHERE book.owner.id = :id
            """
    )
    Page<Book> findAllByOwnerId(Pageable pageable, Integer id);

    @Query(
            """
            SELECT book
            FROM Book book
            JOIN BookTransactionHistory borrowing ON borrowing.book.id = book.id
            WHERE borrowing.borrower.id = :id
            AND borrowing.returned = false
            """
    )
    Page<Book> findAllBorrowedBooks(Pageable pageable, Integer id);
}
