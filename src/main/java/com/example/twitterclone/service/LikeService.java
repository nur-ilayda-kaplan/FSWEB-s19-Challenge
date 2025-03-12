package com.example.twitterclone.service;

import com.example.twitterclone.entity.Like;
import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.exception.ResourceNotFoundException;
import com.example.twitterclone.repository.LikeRepository;
import com.example.twitterclone.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TweetService tweetService;

    @Transactional
    public void toggleLike(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı veya oturum açılmamış");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Tweet tweet = tweetService.findTweetById(tweetId);

        Like existingLike = likeRepository.findByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId);
        if (existingLike != null) {
            // Beğeniyi kaldır
            likeRepository.delete(existingLike);
        } else {
            // Yeni beğeni oluştur
            Like like = new Like();
            like.setTweet(tweet);
            User user = new User();
            user.setId(userPrincipal.getId());
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    public boolean hasUserLikedTweet(Long tweetId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return likeRepository.findByUser_IdAndTweet_Id(userPrincipal.getId(), tweetId) != null;
    }

    public long getTweetLikeCount(Long tweetId) {
        Tweet tweet = tweetService.findTweetById(tweetId);
        if (tweet == null) {
            throw new ResourceNotFoundException("Tweet bulunamadı", "id", tweetId);
        }
        return likeRepository.countByTweet_Id(tweetId);
    }
}
