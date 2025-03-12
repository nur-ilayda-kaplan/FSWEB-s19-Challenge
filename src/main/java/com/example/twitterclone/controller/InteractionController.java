package com.example.twitterclone.controller;

import com.example.twitterclone.service.LikeService;
import com.example.twitterclone.service.RetweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@PreAuthorize("isAuthenticated()")
public class InteractionController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private RetweetService retweetService;

    @PostMapping("/tweets/{tweetId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long tweetId,
            Authentication authentication) {
        likeService.toggleLike(tweetId, authentication);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", likeService.hasUserLikedTweet(tweetId, authentication));
        response.put("likeCount", likeService.getTweetLikeCount(tweetId));
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tweets/{tweetId}/retweet")
    public ResponseEntity<Map<String, Object>> toggleRetweet(
            @PathVariable Long tweetId,
            Authentication authentication) {
        retweetService.toggleRetweet(tweetId, authentication);
        
        Map<String, Object> response = new HashMap<>();
        response.put("retweeted", retweetService.hasUserRetweeted(tweetId, authentication));
        response.put("retweetCount", retweetService.getTweetRetweetCount(tweetId));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tweets/{tweetId}/status")
    public ResponseEntity<Map<String, Object>> getTweetInteractionStatus(
            @PathVariable Long tweetId,
            Authentication authentication) {
        Map<String, Object> status = new HashMap<>();
        status.put("liked", likeService.hasUserLikedTweet(tweetId, authentication));
        status.put("likeCount", likeService.getTweetLikeCount(tweetId));
        status.put("retweeted", retweetService.hasUserRetweeted(tweetId, authentication));
        status.put("retweetCount", retweetService.getTweetRetweetCount(tweetId));
        
        return ResponseEntity.ok(status);
    }
}
