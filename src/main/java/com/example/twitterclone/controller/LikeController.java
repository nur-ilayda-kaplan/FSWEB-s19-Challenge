package com.example.twitterclone.controller;

import com.example.twitterclone.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<?> likeTweet(@RequestParam Long tweetId) {
        return ResponseEntity.ok(likeService.likeTweet(tweetId));
    }

    @PostMapping("/dislike")
    public ResponseEntity<?> dislikeTweet(@RequestParam Long tweetId) {
        likeService.dislikeTweet(tweetId);
        return ResponseEntity.ok().build();
    }
} 