package com.diphlk.book.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feedback")
public class FeedBack extends BaseEntity {

    private String comment;
    private Double note;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
