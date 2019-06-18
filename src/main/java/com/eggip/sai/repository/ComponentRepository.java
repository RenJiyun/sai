package com.eggip.sai.repository;

import com.eggip.sai.domain.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComponentRepository extends JpaRepository<Component, Integer> {
    Optional<Component> findByAcReactiveKey(String acReactiveKey);
}
