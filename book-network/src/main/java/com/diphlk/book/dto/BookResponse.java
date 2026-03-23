package com.diphlk.book.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private boolean shareable;
    private String ownerName;
    private byte[] bookCover;
    private boolean archived;
    private double rate;
}
