package app.security.daos.impl;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.exceptions.ApiException;
import app.populators.UserPopulator;
import app.security.daos.ISecurityDAO;
import app.security.entities.Role;
import app.security.entities.User;
import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityDAOTest {
    private EntityManagerFactory emf;
    private ISecurityDAO securityDAO;

    private UserDTO u1;
    private UserDTO u2;
    private UserDTO u3;

    private List<UserDTO> userDTOs;

    @BeforeAll
    void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        securityDAO = new SecurityDAO(emf);
    }

    @BeforeEach
    void setUp() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE roles, users, users_roles RESTART IDENTITY CASCADE")
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to truncate tables", e);
        }

        // Populate tables
        userDTOs = UserPopulator.populate(securityDAO);
        if (userDTOs.size() == 3) {
            u1 = userDTOs.get(0);
            u2 = userDTOs.get(1);
            u3 = userDTOs.get(2);
        } else {
            throw new ApiException(500, "Populator doesnt work");
        }
    }


    @AfterAll
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }


    @Test
    void getInstance() {
        assertNotNull(emf);
    }


    @Test
    void getVerifiedUser() {
        // Arrange


        // Act
        User verifiedUser = securityDAO.getVerifiedUser("admin", "123"); // u1: Known from populator

        // Assert
        assertThat(u1.getUsername(), is(verifiedUser.getUsername()));
        assertThat(u1.getPassword(), is(verifiedUser.getPassword()));
    }

    @Test
    void createUser() {
        // Arrange

        // Act
        User newUser = securityDAO.createUser("newUser", "123");
        User verifiedUser = securityDAO.getVerifiedUser("newUser", "123");

        // Assert
        assertEquals(newUser, verifiedUser);
    }

    @Test
    void createRole() {
        // Arrange

        // Act
        Role role = securityDAO.createRole("TestRole");

        // Assert
        assertNotNull(role);
        assertEquals("TestRole", role.getRolename());
    }

    @Test
    void addUserRole() {
        // Arrange
        Role testRole = securityDAO.createRole("TestRole");

        // Act
        securityDAO.addUserRole(u1.getUsername(), testRole.getRolename());
        User verifiedUser = securityDAO.getVerifiedUser(u1.getUsername(), "123");

        // Assert
        assertThat(verifiedUser.getRoles().size(), is(2));
    }
}