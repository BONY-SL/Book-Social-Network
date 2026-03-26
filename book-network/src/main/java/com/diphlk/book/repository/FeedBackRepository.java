package com.diphlk.book.repository;

import com.diphlk.book.model.FeedBack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {
    Page<FeedBack> findAllByBookId(Integer bookId, Pageable pageable);
}
