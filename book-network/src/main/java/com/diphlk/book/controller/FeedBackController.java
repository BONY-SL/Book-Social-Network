package com.diphlk.book.controller;

import com.diphlk.book.dto.FeedBackRequest;
import com.diphlk.book.dto.FeedBackResponse;
import com.diphlk.book.dto.PageResponse;
import com.diphlk.book.service.FeedBackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "FeedBack", description = "Feedback API")
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedBack(
            @Valid @RequestBody FeedBackRequest feedBackRequest,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(feedBackService.saveFeedBack(feedBackRequest, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedBackResponse>> getAllFeedBacksByBookId(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page",defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(feedBackService.getAllFeedBacksByBookId(bookId, page, size, connectedUser));
    }
}
