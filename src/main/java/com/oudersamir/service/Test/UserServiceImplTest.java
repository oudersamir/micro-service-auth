package com.oudersamir.service.Test;

import com.oudersamir.dao.RoleRepository;
import com.oudersamir.dao.UserRepository;
import com.oudersamir.entities.RoleEntity;
import com.oudersamir.entities.UserEntity;
import com.oudersamir.exception.BusinessResourceException;
import com.oudersamir.service.UserService;
import com.oudersamir.service.UserServiceImpl;
import com.oudersamir.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserServiceImplTest {
    private UserService userService;
    private Utils utils;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Before
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);

        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        utils = Mockito.mock(Utils.class);

        userService = new UserServiceImpl(userRepository, roleRepository, bCryptPasswordEncoder,utils);
    }
    @Test
    public void testFindAllUsers() throws Exception {
        UserEntity  user = new UserEntity(null, "asmae", "jalni", "jfn3k4jt5bg3bjgkb3kjb35gkb56"
                , "jalniasmae", "jalniasmae"
                , "asmaejalni@gmail.com", false,
                null );
        RoleEntity role = new RoleEntity(null,"USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        List<UserEntity> allUsers = Arrays.asList(user);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        Collection<UserEntity> users = userService.getAllUser();
        assertNotNull(users);
        assertEquals(users, allUsers);
        assertEquals(users.size(), allUsers.size());
        verify(userRepository).findAll();
    }
    @Test
    public void testSaveUser() throws Exception {
        UserEntity  user = new UserEntity(11L, "asmae", "jalni", null
                , "jalniasmae", "jalniasmae"
                , "asmaejalni@gmail.com", false,
                null );
        RoleEntity role = new RoleEntity(null,"USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        UserEntity  userMock = new UserEntity(11L, "asmae", "jalni", "jkjhjgjg786785tgy"
                , "jalniasmae", "jalniasmae"
                , "asmaejalni@gmail.com", false,
                null );
        userMock.setRoles(roles);
        Mockito.when(roleRepository.getAllRolesStreams()).thenReturn(roles.stream());
        Mockito.when(userRepository.save((user))).thenReturn(userMock);
        UserEntity userSaved = userService.saveOrUpdate(user);
        assertNotNull(userSaved);
        assertEquals(userMock.getId(), userSaved.getId());
        assertEquals(userMock.getUserName(), userSaved.getUserName());
        assertEquals(userMock.getRoles(), userSaved.getRoles());
        verify(userRepository).save(any(UserEntity.class));
    }
   // @Test(expected= BusinessResourceException.class)
    public void testSaveUser_existing_login_throws_error() throws Exception {
        UserEntity  user = new UserEntity(null, "asmae", "jalni", null
                , "jalniasmae", "jalniasmae"
                , "asmaejalni@gmail.com", false,
                null );
        RoleEntity role = new RoleEntity(null,"USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        Mockito.when(roleRepository.getAllRolesStreams()).thenReturn(roles.stream());
        Mockito.when(userRepository.save((user))).thenThrow(new DataIntegrityViolationException("Duplicate Login"));
        userService.saveOrUpdate(user);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void testFindUserByUsername() {
        UserEntity  user = new UserEntity(null, "asmae", "jalni", null
                , "jalniasmae", "jalniasmae"
                , "asmaejalni@gmail.com", false,
                null );
        Mockito.when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.ofNullable(user));
        Optional<UserEntity> userFromDB = userService.findByUserName(user.getUserName());
        assertNotNull(userFromDB);
       org.hamcrest.MatcherAssert.assertThat(userFromDB.get().getUserName(), is(user.getUserName()));
        verify(userRepository).findByUserName(any(String.class));
    }
  //  @Test
    public void testUpdateUser() throws Exception {
        UserEntity userToUpdate =  new UserEntity(100L, "nejma", "sadik", "wwefwe879789we"
                , "sadiknejma", "sadiknejma"
                , "sadiknejma@gmail.com", true,
                null );
        RoleEntity role = new RoleEntity(null,"USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);

        UserEntity userFoundById =  new UserEntity(100L, "nejma", "sadik", "wwefwe879789we"
                , "nejmasadik", "nejmasadik"
                , "sadiknejma@gmail.com", false,
                null );
        userFoundById.setRoles(roles);

        UserEntity userUpdated = new UserEntity(100L, "nejma", "sadik", "wwefwe879789we"
                , "sadiknejma", "sadiknejma"
                , "sadiknejma@gmail.com", true,
                null );
        userUpdated.setRoles(roles);

        Mockito.when(userRepository.findByUserId("wwefwe879789we")).thenReturn(Optional.of(userFoundById));
        Mockito.when(bCryptPasswordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);
        Mockito.when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("sadiknejma");
        Mockito.when(userRepository.save((userToUpdate))).thenReturn(userUpdated);
        UserEntity userFromDB = userService.saveOrUpdate(userToUpdate);
        assertNotNull(userFromDB);
        verify(userRepository).save(any(UserEntity.class));
        assertEquals(new Long(100L), userFromDB.getId());
        assertEquals("sadiknejma", userFromDB.getUserName());
        assertEquals("sadiknejma", userFromDB.getPassword());
        assertEquals(new Boolean(true), userFromDB.isActivated());
        assertEquals(roles, userFromDB.getRoles());
    }
    //@Test
    public void testDelete() throws Exception {
        UserEntity userTodelete = new UserEntity(4L, "nejma", "sadik", "MVqLQlWSHC4BDsl7q5hJvI2EMZmYFd"
                , "sadiknejma", "sadiknejma"
                , "sadiknejma@gmail.com", true,
                null);

        Mockito.doNothing().when(userRepository).deleteByUserId(userTodelete.getUserId());
        userService.deleteUser(userTodelete.getUserId());
        verify(userRepository).deleteByUserId(any(String.class));
    }
}
