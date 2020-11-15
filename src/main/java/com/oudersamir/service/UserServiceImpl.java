package com.oudersamir.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;




import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oudersamir.dao.RoleRepository;
import com.oudersamir.dao.UserRepository;
import com.oudersamir.entities.RoleEntity;
import com.oudersamir.entities.UserEntity;
import com.oudersamir.exception.BusinessResourceException;
import com.oudersamir.shared.Utils;
@Service
public class UserServiceImpl implements UserService{
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private Utils utils;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository,
		com.oudersamir.dao.RoleRepository roleRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder) {
	super();
	this.userRepository = userRepository;
	this.roleRepository = roleRepository;
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
}

	@Override
	public Collection<UserEntity> getAllUser() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	@Override
	public Optional<UserEntity> getUserById(Long id) throws BusinessResourceException {
		Optional<UserEntity> userFound=userRepository.findById(id);
		if(Boolean.FALSE.equals(userFound.isPresent())){
			throw new BusinessResourceException("User Not Found", "Aucun utilisateur avec l'identifiant : " + id);
		}
		return userFound;
	}
	@Override
	public Optional<UserEntity> findByUserName(String userName) throws BusinessResourceException{
		Optional<UserEntity> userFound=userRepository.findByUserName(userName);
		if(Boolean.FALSE.equals(userFound.isPresent())){
			throw new BusinessResourceException("User Not Found", "Aucun utilisateur avec username : " + userName);
		}
		return userFound;
	}

	@Override
	public Optional<UserEntity> findByUserId(String userId) throws BusinessResourceException{
		Optional<UserEntity> userFound=userRepository.findByUserId(userId);
		if(Boolean.FALSE.equals(userFound.isPresent())){
			throw new BusinessResourceException("UserNotFound", "Aucun utilisateur n'existe avec l'identifiant : "+userId
					, HttpStatus.NOT_FOUND);
		}
		return userFound;
	}

	@Override
	public Optional<UserEntity> findByEmail(String email) throws BusinessResourceException{
		Optional<UserEntity> userFound=userRepository.findByEmail(email);
		if(Boolean.FALSE.equals(userFound.isPresent())){
			throw new BusinessResourceException("User Not Found", "Aucun utilisateur avec l'email :   " +email, HttpStatus.NOT_FOUND);
		}
		return userFound;
	}

	@Override
	@Transactional(readOnly=false)
	public UserEntity saveOrUpdate(UserEntity user)throws BusinessResourceException {
		UserEntity result=null;
		if(user.getUserId()==null){
			Optional<UserEntity> userFromDb=userRepository.findByEmail(user.getEmail());	
			if(userFromDb.isPresent()){
				throw new BusinessResourceException("UserAlreadyExist", "l'email deja utilise : "+user.getEmail()
						, HttpStatus.NOT_ACCEPTABLE);			
				}else {
				addUserRole(user);
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
				user.setUserId(utils.generateStringId(30));
				result=userRepository.save(user);	
			}
		}
		else {
			Optional<UserEntity> userFromDb=userRepository.findByUserName(user.getUserName());
			if(!bCryptPasswordEncoder.matches(user.getPassword(),userFromDb.get().getPassword())){
				userFromDb.get().setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			//userFromDb.get().setEmail(user.getEmail());
			userFromDb.get().setLastName(user.getLastName());
			userFromDb.get().setFirstName(user.getFirstName());
			//userFromDb.get().setUserName(user.getUserName());
			updateUserRole(user);
			userFromDb.get().setRoles(user.getRoles());
			result=userRepository.save(userFromDb.get());
		}
		return result;
	}

	@Override
	public void deleteUser(String userId) throws BusinessResourceException{
		// TODO Auto-generated method stub
		try{
			UserEntity user=findByUserId(userId).get();
			userRepository.deleteById(user.getId());
		}catch(EmptyResultDataAccessException ex){
			logger.error(String.format("Aucun utilisateur n'existe avec l'identifiant: "+userId, ex));
			throw new BusinessResourceException("DeleteUserError", "Erreur de suppression de l'utilisateur avec l'identifiant : "+userId, HttpStatus.NOT_FOUND);
		}catch(Exception ex){
			throw new BusinessResourceException("DeleteUserError", "Erreur de suppression de l'utilisateur avec l'identifiant : "+userId, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@Override
	public Optional<UserEntity> findByUserNameAndPassword(String userName,
			String password)throws BusinessResourceException {
		Optional<UserEntity> userFromDb=null;
		try{
		 userFromDb=findByUserName(userName);
		if(!userFromDb.isPresent())
		throw new BusinessResourceException("UserNotFound", "Aucun utilisateur n'existe avec le nom "+userName
				, HttpStatus.NOT_FOUND);

		if(!bCryptPasswordEncoder.matches(password, userFromDb.get().getPassword()))
			throw new BusinessResourceException("UserNotFound", " mot de passe incorrect", HttpStatus.NOT_FOUND);		
	
		} catch (BusinessResourceException ex) {
			logger.error("Login ou mot de passe incorrect", ex);
			throw new BusinessResourceException("UserNotFound", ex.getMessage(), HttpStatus.NOT_FOUND);
		}catch (Exception ex) {
			logger.error("Une erreur technique est survenue", ex);
			throw new BusinessResourceException("TechnicalError", "Une erreur technique est survenue", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userFromDb;
	}

	private void addUserRole(UserEntity user) {
		// TODO Auto-generated method stub
		RoleEntity role= new RoleEntity(null,"USER");
		Set<RoleEntity> roles=new HashSet<>();
		roles.add(role);
		Set<RoleEntity> rolesToDb=extractRoleFromDB(roles, roleRepository.getAllRolesStreams());
		user.setRoles(rolesToDb);
	}
	private Set<RoleEntity> extractRoleFromDB(Set<RoleEntity> roles,Stream<RoleEntity> rolesDb){
		
	return rolesDb.filter(roleDb->roles.stream()
			.anyMatch(role->role.equals(roleDb)))
			.collect(Collectors.toCollection(HashSet::new));	
	}
	
	private void updateUserRole(UserEntity user){
		
		Set<RoleEntity> roles=extractRoleFromDB(user.getRoles().stream()
				.collect(Collectors.toCollection(HashSet::new))
				, roleRepository.getAllRolesStreams());
		user.setRoles(roles);
	}
	

	

	

}
