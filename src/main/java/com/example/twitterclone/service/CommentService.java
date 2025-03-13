package com.example.twitterclone.service;

import com.example.twitterclone.dto.CommentRequest;
import com.example.twitterclone.entity.Comment;
import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.repository.CommentRepository;
import com.example.twitterclone.repository.TweetRepository;
import com.example.twitterclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment createComment(CommentRequest request) {
        User currentUser = getCurrentUser();
        Tweet tweet = tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(currentUser);
        comment.setTweet(tweet);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User currentUser = getCurrentUser();
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own comments");
        }

        if (!comment.getTweet().getId().equals(request.getTweetId())) {
            throw new RuntimeException("Cannot change the tweet of a comment");
        }

        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User currentUser = getCurrentUser();
        if (!comment.getUser().getId().equals(currentUser.getId()) && 
            !comment.getTweet().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own comments or comments on your tweets");
        }

        commentRepository.delete(comment);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 