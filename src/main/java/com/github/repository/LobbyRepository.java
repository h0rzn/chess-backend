package com.github.repository;

import com.github.entity.LobbyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LobbyRepository extends CrudRepository<LobbyEntity, Integer> {
}
