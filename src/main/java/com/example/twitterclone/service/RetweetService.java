package com.example.twitterclone.service;

import com.example.twitterclone.entity.Retweet;
import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.exception.ResourceNotFoundException;
import com.example.twitterclone.repository.RetweetRepository;
import com.example.twitterclone.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetweetService {

    @Autowired
    private RetweetRepository retweetRepository;

    @Autowired
    private TweetService tweetService;

    @Transactional
    public void toggleRetweet(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı veya oturum açılmamış");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Tweet tweet = tweetService.findTweetById(tweetId);

        Retweet existingRetweet = retweetRepository.findByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId);
        if (existingRetweet != null) {
            // Retweet'i kaldır
            retweetRepository.delete(existingRetweet);
        } else {
            // Yeni retweet oluştur
            Retweet retweet = new Retweet();
            retweet.setTweet(tweet);
            User user = new User();
            user.setId(userPrincipal.getId());
            retweet.setUser(user);
            retweetRepository.save(retweet);
        }
    }

    public boolean hasUserRetweeted(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return retweetRepository.findByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId) != null;
    }

    public long getTweetRetweetCount(Long tweetId) {
        Tweet tweet = tweetService.findTweetById(tweetId);
        if (tweet == null) {
            throw new ResourceNotFoundException("Tweet bulunamadı", "id", tweetId);
        }
        return retweetRepository.countByTweet_Id(tweetId);
    }
}
