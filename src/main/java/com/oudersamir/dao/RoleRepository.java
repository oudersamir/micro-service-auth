package com.oudersamir.dao;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.oudersamir.entities.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity,Long> {
@Query("select role from RoleEntity role")
Stream<RoleEntity> getAllRolesStreams();
Optional<RoleEntity> findByRoleName(String roleName);
}
