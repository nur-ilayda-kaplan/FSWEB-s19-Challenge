package com.example.twitterclone.service;

import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.repository.TweetRepository;
import com.example.twitterclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TweetService tweetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTweet_ShouldReturnCreatedTweet() {
        // Arrange
        String content = "Test tweet";
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Tweet result = tweetService.createTweet(content);

        // Assert
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(user, result.getUser());
        verify(tweetRepository).save(any(Tweet.class));
    }
} 