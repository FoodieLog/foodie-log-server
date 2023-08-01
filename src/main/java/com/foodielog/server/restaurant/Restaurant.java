package com.foodielog.server.restaurant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "restaurant_tb")
@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private int mapX;

    @Column(nullable = false)
    private int mapY;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String roadAddress;
}
