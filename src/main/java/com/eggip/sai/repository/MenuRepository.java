package com.eggip.sai.repository;

import com.eggip.sai.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Resource, Integer> {
}
