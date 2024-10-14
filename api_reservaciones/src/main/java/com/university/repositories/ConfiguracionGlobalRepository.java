package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.ConfiguracionGlobal;

import java.util.Optional;

@Repository
public interface ConfiguracionGlobalRepository extends CrudRepository<ConfiguracionGlobal, Long> {
    Optional<ConfiguracionGlobal> findFirstByOrderByIdAsc();
}
