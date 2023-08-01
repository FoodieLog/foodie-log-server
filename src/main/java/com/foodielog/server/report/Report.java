package com.foodielog.server.report;

import com.foodielog.server.types.ReportType;
import com.foodielog.server.types.ReportReason;
import com.foodielog.server.types.ManagementStatus;
import com.foodielog.server.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "report_tb")
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporterId;

    @ManyToOne
    @JoinColumn(name = "reported_id")
    private User reportedId;

    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ManagementStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
