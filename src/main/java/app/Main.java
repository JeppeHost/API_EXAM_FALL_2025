package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.populators.Populator;
import app.populators.UserPopulator;
import app.security.daos.ISecurityDAO;
import app.security.daos.impl.SecurityDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        ISecurityDAO securityDAO = new SecurityDAO(emf);
        UserPopulator.populate(securityDAO);
        Populator.populate(emf);
        ApplicationConfig.startServer(7070);
    }
}