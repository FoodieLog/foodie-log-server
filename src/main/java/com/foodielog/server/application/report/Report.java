package com.foodielog.server.application.report;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.foodielog.server.application.report.type.ReportReason;
import com.foodielog.server.application.report.type.ReportType;
import com.foodielog.server.application.user.entity.User;
import com.foodielog.server.management.admin.type.ProcessedStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private ProcessedStatus status;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;
}
