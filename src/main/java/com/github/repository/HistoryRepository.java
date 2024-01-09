package com.github.repository;

import com.github.entity.HistoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing history, Methods are autowired by Spring
 */
@Repository
public interface HistoryRepository extends CrudRepository<HistoryEntity, String> {
}
