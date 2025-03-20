package com.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigRepository extends JpaRepository<Config, Integer> {
    Optional<Config> findByName(String name);
}