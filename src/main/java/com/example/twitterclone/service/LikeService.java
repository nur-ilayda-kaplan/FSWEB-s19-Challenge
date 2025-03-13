package com.example.twitterclone.service;

import com.example.twitterclone.entity.Like;
import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.exception.ResourceNotFoundException;
import com.example.twitterclone.repository.LikeRepository;
import com.example.twitterclone.repository.TweetRepository;
import com.example.twitterclone.repository.UserRepository;
import com.example.twitterclone.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, TweetRepository tweetRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggleLike(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new RuntimeException("Kullanıcı bulunamadı veya oturum açılmamış");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        if (likeRepository.existsByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId)) {
            // Beğeniyi kaldır
            likeRepository.deleteByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId);
        } else {
            // Yeni beğeni oluştur
            Like like = new Like();
            like.setTweet(tweet);
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    public boolean hasUserLikedTweet(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return likeRepository.existsByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId);
    }

    public long getTweetLikeCount(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet bulunamadı", "id", tweetId));
        return likeRepository.countByTweet_Id(tweetId);
    }

    @Transactional
    public Like likeTweet(Long tweetId) {
        User currentUser = getCurrentUser();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        if (likeRepository.existsByUserAndTweet(currentUser, tweet)) {
            throw new RuntimeException("You have already liked this tweet");
        }

        Like like = new Like();
        like.setUser(currentUser);
        like.setTweet(tweet);

        return likeRepository.save(like);
    }

    @Transactional
    public void dislikeTweet(Long tweetId) {
        User currentUser = getCurrentUser();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Like like = likeRepository.findByUserAndTweet(currentUser, tweet)
                .orElseThrow(() -> new RuntimeException("You have not liked this tweet"));

        likeRepository.delete(like);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
