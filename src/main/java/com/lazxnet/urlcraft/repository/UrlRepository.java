package com.lazxnet.urlcraft.repository;

import com.lazxnet.urlcraft.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {
    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);

    @Query("SELECT COUNT(u) > 0 FROM Url u WHERE u.shortCode = :shortCode AND u.id != :id")
    boolean existsByShortCodeAndIdNot(@Param("shortCode") String shortCode,@Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Url u WHERE u.shortCode = :shortCode")
    void deleteByShortCode(@Param("shortCode") String shortCode);
}
