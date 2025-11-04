package app.populators;

import app.security.daos.ISecurityDAO;
import app.security.entities.Role;
import app.security.entities.User;
import dk.bugelhartmann.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserPopulator {

    public static List<UserDTO> populate (ISecurityDAO securityDAO) {
        List<UserDTO> userDTOs = new ArrayList<>();

        // Create and save roles in DB
        Role r1 = new Role("Admin");
        Role r2 = new Role("User");
        Role r3 = new Role("Anyone");
        securityDAO.createRole("Admin");
        securityDAO.createRole("User");
        securityDAO.createRole("Anyone");

        // Save user in DB
        User u1 = securityDAO.createUser("admin", "123");
        User u2 = securityDAO.createUser("user", "123");
        User u3 = securityDAO.createUser("user2", "123");

        // Add roles to users
        securityDAO.addUserRole("admin", "Admin");
        securityDAO.addUserRole("user", "User");
        securityDAO.addUserRole("user2", "User");

        // Convert to DTOs and add to List
        UserDTO userDTO1 = new UserDTO(u1.getUsername(), u1.getPassword());
        UserDTO userDTO2 = new UserDTO(u2.getUsername(), u2.getPassword());
        UserDTO userDTO3 = new UserDTO(u3.getUsername(), u3.getPassword());

        userDTOs.add(userDTO1);
        userDTOs.add(userDTO2);
        userDTOs.add(userDTO3);

        return userDTOs;
    }
}
