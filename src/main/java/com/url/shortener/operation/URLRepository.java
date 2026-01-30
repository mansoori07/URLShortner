package com.url.shortener.operation;

import com.url.shortener.models.URLMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<URLMapping,Long> {

    Optional<URLMapping> findByShortCode(String shortCode);

    @Modifying
    @Transactional
    @Query("UPDATE URLMapping u SET u.clicks = u.clicks + :clickCount WHERE u.shortCode = :shortCode")
    void updateClickCount(String shortCode, Long clickCount);
}
