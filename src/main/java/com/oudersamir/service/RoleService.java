package com.oudersamir.service;

import java.util.Collection;
import java.util.stream.Stream;

import com.oudersamir.entities.RoleEntity;

public interface RoleService {
 RoleEntity findByRoleName(String roleName);
 Collection<RoleEntity> getAllRoles();
 Stream<RoleEntity> getAllRolesStream();
}
