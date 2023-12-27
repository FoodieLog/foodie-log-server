package com.foodielog.server.admin.repository;

import com.foodielog.server.admin.entity.WithdrawUser;
import com.foodielog.server.user.type.Flag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WithdrawUserRepository extends JpaRepository<WithdrawUser, Long> {

    @Query("SELECT w FROM WithdrawUser w " +
            "WHERE (:nickName IS NULL OR w.user.nickName LIKE %:nickName%) " +
            "AND (:badgeFlag IS NULL OR w.user.badgeFlag = :badgeFlag)")
    List<WithdrawUser> findByFlag(Pageable pageable, @Param("nickName") String nickName, @Param("badgeFlag") Flag flag);
}
