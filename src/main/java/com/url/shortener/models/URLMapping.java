package com.url.shortener.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "url_mappings", indexes = @Index(columnList = "shortCode"))
public class URLMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String longUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    private long clicks = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

}
