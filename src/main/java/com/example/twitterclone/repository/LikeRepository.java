package com.example.twitterclone.repository;

import com.example.twitterclone.entity.Like;
import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndTweet(User user, Tweet tweet);
    Optional<Like> findByUserAndTweet(User user, Tweet tweet);
    long countByTweet_Id(Long tweetId);
    boolean existsByUser_IdAndTweet_Id(Long userId, Long tweetId);
    void deleteByUser_IdAndTweet_Id(Long userId, Long tweetId);
}