package com.oudersamir.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oudersamir.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long>{
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByUserName(String userName);
	Optional<UserEntity> findByUserId(String userId);
	void deleteByUserId(String userId);


}
