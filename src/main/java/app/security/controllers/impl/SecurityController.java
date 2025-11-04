package app.security.controllers.impl;


import app.config.HibernateConfig;
import app.security.exceptions.ApiException;
import app.security.controllers.ISecurityController;
import app.security.daos.ISecurityDAO;
import app.security.daos.impl.SecurityDAO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.NotAuthorizedException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.JOSEException;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.*;
import jakarta.persistence.EntityExistsException;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;


public class SecurityController implements ISecurityController {
    private static ISecurityDAO securityDAO;
    private static SecurityController instance;
    ObjectMapper objectMapper = new ObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();

    private SecurityController() { }

    public static SecurityController getInstance() {
        if (instance == null) {
            instance = new SecurityController();
        }
        securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }


    @Override
    public Handler login() {
        return (Context ctx) -> {
            User user = ctx.bodyAsClass(User.class);

            User checkedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());

            Set<String> roles = checkedUser
                    .getRoles()
                    .stream()
                    .map(role -> role.getRolename())
                    .collect(Collectors.toSet());

            UserDTO userDTOForToken = new UserDTO(checkedUser.getUsername(), roles);

            String token = createToken(userDTOForToken);

            ObjectNode response = objectMapper.createObjectNode()
                    .put("token", token)
                    .put("username", userDTOForToken.getUsername());
            ctx.json(response).status(200);
        };
    }

    @Override
    public Handler register() {
        return (Context ctx) -> {
            User user = ctx.bodyAsClass(User.class);

            Role userRole = new Role("User");
            user.addRole(userRole);

            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                User createdUser = securityDAO.createUser(user.getUsername(), user.getPassword());

                User createdUserWithRole = securityDAO.addUserRole(createdUser.getUsername(), "User");

                Set<String> roles = createdUserWithRole
                        .getRoles()
                        .stream()
                        .map(role -> role.getRolename())
                        .collect(Collectors.toSet());

                UserDTO userDTOForToken = new UserDTO(createdUser.getUsername(), roles);

                String token = createToken(userDTOForToken);

                ObjectNode response = objectMapper
                        .createObjectNode()
                        .put("token", token)
                        .put("username", userDTOForToken.getUsername());
                ctx.json(response).status(200);
            } catch (EntityExistsException e) {
                ctx.status(HttpStatus.CONFLICT);
                ctx.json(returnObject.put("msg", "Username is taken, try another"));
            }
        };
    }

    // Checks the user has a valid token
    @Override
    public Handler authenticate() {
        return (Context ctx) -> {

            // Pre-flight request
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }


            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            if (isOpenEndpoint(allowedRoles))
                return;

            UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);
            ctx.attribute("user", verifiedTokenUser); // -> ctx.attribute("user") in ApplicationConfig beforeMatched filter
        };
    }


    // Checks if user has the allowed roles for an endpoint
    @Override
    public Handler authorize() {
        return (Context ctx) -> {

            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            if (isOpenEndpoint(allowedRoles))
                return;

            UserDTO user = ctx.attribute("user");
            if (user == null) {
                throw new ForbiddenResponse("No user was added from the token");
            }

            if (!userHasAllowedRole(user, allowedRoles))
                throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        };
    }


    // Private extra helping methods

    private String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;


            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");

            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }

            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            //    logger.error("Could not create token", e);
            throw new ApiException(500, "Could not create token");
        }
    }


    private static String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null || header.isBlank()) {
            throw new UnauthorizedResponse("Authorization header is missing");
        }

        String[] headerParts = header.split(" ");
        if (headerParts.length != 2) {
            throw new UnauthorizedResponse("Authorization header malformed, expected format 'Bearer <JWT_token>', try logging in ");
        }

        String token = headerParts[1];
        return token;
    }

    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token");
        }
        return verifiedTokenUser;
    }


    private UserDTO verifyToken(String token) {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ?
                System.getenv("SECRET_KEY")
                :
                Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | NotAuthorizedException | JOSEException e) {
            // logger.error("Could not create token", e);
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }


    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        if (allowedRoles.isEmpty())
            return true;

        if (allowedRoles.contains("ANYONE")) {
            return true;
        }

        return false;
    }

    private static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        return user.getRoles()
                .stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }

    // Health check for the API. Used in deployment
    public void healthCheck(Context ctx) {
        ctx.status(200).json("{\"msg\": \"API is up and running\"}");
    }
}
