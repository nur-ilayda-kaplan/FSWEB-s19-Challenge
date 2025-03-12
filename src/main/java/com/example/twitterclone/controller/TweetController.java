package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@PreAuthorize("isAuthenticated()")//Anonim tweetlere izin verilmez
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@Valid @RequestBody Tweet tweet, Authentication authentication) {
        Tweet createdTweet = tweetService.createTweet(tweet, authentication);
        return ResponseEntity.ok(createdTweet);
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<List<Tweet>> findTweetsByUserId(@RequestParam Long userId) {
        List<Tweet> tweets = tweetService.findTweetsByUserId(userId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/findById")
    public ResponseEntity<Tweet> findTweetById(@RequestParam Long id) {
        Tweet tweet = tweetService.findTweetById(id);
        return ResponseEntity.ok(tweet);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@tweetService.isTweetOwner(#id, authentication)")
    public ResponseEntity<Tweet> updateTweet(
            @PathVariable Long id,
            @Valid @RequestBody Tweet tweetDetails,
            Authentication authentication) {
        Tweet updatedTweet = tweetService.updateTweet(id, tweetDetails, authentication);
        return ResponseEntity.ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@tweetService.isTweetOwner(#id, authentication)")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id, Authentication authentication) {
        tweetService.deleteTweet(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
