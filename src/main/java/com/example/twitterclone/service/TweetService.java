package com.example.twitterclone.service;

import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.exception.ResourceNotFoundException;
import com.example.twitterclone.repository.TweetRepository;
import com.example.twitterclone.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    public Tweet createTweet(Tweet tweet, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı veya oturum açılmamış");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = new User();
        user.setId(userPrincipal.getId());
        tweet.setUser(user);
        return tweetRepository.save(tweet);
    }

    public List<Tweet> findTweetsByUserId(Long userId) {
        return tweetRepository.findByUser_Id(userId);
    }

    public Tweet findTweetById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet bulunamadı", "id", id));
    }

    public Tweet updateTweet(Long id, Tweet tweetDetails, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı veya oturum açılmamış");
        }

        Tweet tweet = findTweetById(id);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        if (!tweet.getUser().getId().equals(userPrincipal.getId())) {
            throw new RuntimeException("Bu tweet'i güncelleme yetkiniz yok");
        }

        tweet.setContent(tweetDetails.getContent());
        return tweetRepository.save(tweet);
    }

    public void deleteTweet(Long id, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı veya oturum açılmamış");
        }

        Tweet tweet = findTweetById(id);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        if (!tweet.getUser().getId().equals(userPrincipal.getId())) {
            throw new RuntimeException("Bu tweet'i silme yetkiniz yok");
        }

        tweetRepository.delete(tweet);
    }

    public boolean isTweetOwner(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return false;
        }

        try {
            Tweet tweet = findTweetById(tweetId);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return tweet.getUser().getId().equals(userPrincipal.getId());
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
