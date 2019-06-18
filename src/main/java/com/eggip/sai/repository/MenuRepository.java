package com.eggip.sai.repository;

import com.eggip.sai.domain.Component;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Component, Integer> {
}
