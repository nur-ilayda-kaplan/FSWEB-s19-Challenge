package com.example.twitterclone.repository;

import com.example.twitterclone.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByUser_IdAndTweet_Id(Long userId, Long tweetId);

    int countByTweet_Id(Long tweetId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.tweet.id = :tweetId AND l.user.id = :userId")
    boolean existsByTweet_IdAndUser_Id(@Param("tweetId") Long tweetId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.tweet.id = :tweetId AND l.user.id = :userId")
    void deleteByTweet_IdAndUser_Id(@Param("tweetId") Long tweetId, @Param("userId") Long userId);
}