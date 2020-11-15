package com.oudersamir.service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oudersamir.dao.RoleRepository;
import com.oudersamir.entities.RoleEntity;
@Service
public class RoleServiceImpl  implements RoleService{
	
    private RoleRepository roleRepository;
    @Autowired
	public RoleServiceImpl(RoleRepository roleRepository) {
		super();
		this.roleRepository = roleRepository;
	}

	@Override
	public RoleEntity findByRoleName(String roleName) {
		// TODO Auto-generated method stub
		Optional<RoleEntity> roleFound=roleRepository.findByRoleName(roleName);
		if(roleFound.isPresent())
			return roleFound.get();
		throw new RuntimeException("role not found !!");
	}

	@Override
	public Collection<RoleEntity> getAllRoles() {
		// TODO Auto-generated method stub
		return roleRepository.findAll();
	}

	@Override
	public Stream<RoleEntity> getAllRolesStream() {
		// TODO Auto-generated method stub
		return roleRepository.getAllRolesStreams();
	}

}
