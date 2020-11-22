package com.oudersamir.doa.Test;

import com.oudersamir.dao.UserRepository;
import com.oudersamir.entities.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)//permet d'établir une liaison entre JUnit et Spring
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    UserEntity  user = new UserEntity(null, "asmae", "jalni", "jfn3k4jt5bg3bjgkb3kjb35gkb56"
            , "jalniasmae", "jalniasmae"
            , "asmaejalni@gmail.com", false,
            null );
    @Before
    public  void setup(){
       // user.setRoles(Arrays.asList(new RoleEntity(null,"USER")).stream().collect(Collectors.toSet()) );
        entityManager.persist(user);//on sauvegarde l'objet user au début de chaque test
        entityManager.flush();
    }

    @Test
    public  void testFindAllUsers(){
      List<UserEntity> users= userRepository.findAll() ;
      org.hamcrest.MatcherAssert.assertThat(3,is(users.size()));//on a 2 Users dans la base de donnees et un utilisateur ajouté lors du setup du test

    }
    @Test
    public void testSaveUser(){
        UserEntity  userSaved = userRepository.save(user);
        assertNotNull(userSaved.getId());
        org.hamcrest.MatcherAssert.assertThat("jalniasmae",is(userSaved.getUserName()));
    }
    @Test
    public void testFindByUserName(){
       UserEntity userFound= userRepository.findByUserName("oudersamir").get();
       org.hamcrest.MatcherAssert.assertThat("oudersamir",is(userFound.getUserName()));
    }
    @Test
    public void testFindByUserId(){
        UserEntity userEntity=userRepository.findByUserId(user.getUserId()).get();
        org.hamcrest.MatcherAssert.assertThat(user.getUserName(),is(userEntity.getUserName()));

    }
    @Test
    public void testFindBy_Unknow_Id(){
        Optional<UserEntity> userFromDb=userRepository.findById(50L);
        assertEquals(Optional.empty(),Optional.ofNullable(userFromDb).get());
    }
    @Test
    public void testDeleteUser(){
        userRepository.deleteById(user.getId());
        Optional<UserEntity> userFromDb=userRepository.findByUserName(user.getUserName());
        assertEquals(Optional.empty(),Optional.ofNullable(userFromDb).get());
    }
    @Test
    public void testUpdateUser(){//Test si le compte utilisateur est activé
        Optional<UserEntity> userFromDb=userRepository.findByUserName(user.getUserName());
        userFromDb.get().setActivated(true);
        userRepository.save(userFromDb.get());
        Optional<UserEntity> userUpdateFromDb=userRepository.findByUserName(userFromDb.get().getUserName());
        assertNotNull(userUpdateFromDb);
        org.hamcrest.MatcherAssert.assertThat(true,is(userUpdateFromDb.get().isActivated()));

    }
}
