package com.foodielog.server.mention.repository;

import com.foodielog.server.mention.entity.Mention;
import com.foodielog.server.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MentionRepository extends JpaRepository<Mention, Long> {

    @Query("SELECT m FROM Mention m " +
            "JOIN FETCH m.mentionedUser " +
            "WHERE m.reply = :reply ")
    List<Mention> findByReply(Reply reply);
}
