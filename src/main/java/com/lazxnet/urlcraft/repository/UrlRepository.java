package com.lazxnet.urlcraft.repository;

import com.lazxnet.urlcraft.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {
    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
