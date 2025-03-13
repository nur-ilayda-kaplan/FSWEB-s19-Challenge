package com.example.twitterclone.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long tweetId;
} 