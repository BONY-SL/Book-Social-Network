package com.diphlk.book.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponse {
    private String comment;
    private Double note;
    private boolean isMine;
}
