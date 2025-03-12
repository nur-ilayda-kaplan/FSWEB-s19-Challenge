package com.example.twitterclone.repository;

import com.example.twitterclone.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    Retweet findByUser_IdAndTweet_Id(Long userId, Long tweetId);

    int countByTweet_Id(Long tweetId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Retweet r WHERE r.tweet.id = :tweetId AND r.user.id = :userId")
    void deleteByTweet_IdAndUser_Id(@Param("tweetId") Long tweetId, @Param("userId") Long userId);
}