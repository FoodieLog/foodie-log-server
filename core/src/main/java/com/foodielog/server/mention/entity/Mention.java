package com.foodielog.server.mention.entity;

import com.foodielog.server.reply.entity.Reply;
import com.foodielog.server.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
@Table(name = "mention_tb")
@Entity
public class Mention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioner_id")
    private User mentioner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_id")
    private User mentioned;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public static Mention createMention(Reply reply, User mentioner, User mentioned) {
        Mention mention = new Mention();
        mention.reply = reply;
        mention.mentioner = mentioner;
        mention.mentioned = mentioned;
        return mention;
    }
}
