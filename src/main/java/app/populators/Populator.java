package app.populators;

import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class Populator {

    public static void populate(EntityManagerFactory emf) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // --- Kandidater ---
        Candidate c1 = Candidate.builder()
                .name("Alice Andersen")
                .phone("12345678")
                .education("B.Sc. Computer Science")
                .build();

        Candidate c2 = Candidate.builder()
                .name("Bob Jensen")
                .phone("87654321")
                .education("Datamatiker")
                .build();

        em.persist(c1);
        em.persist(c2);

        // --- Skills fra tabellen ---

        // PROG_LANG
        Skill java = Skill.builder().name("Java").skillCategory(SkillCategory.PROG_LANG).description("General-purpose programming languages").build();
        Skill python = Skill.builder().name("Python").skillCategory(SkillCategory.PROG_LANG).description("General-purpose programming languages").build();
        Skill csharp = Skill.builder().name("C#").skillCategory(SkillCategory.PROG_LANG).description("General-purpose programming languages").build();
        Skill javascript = Skill.builder().name("JavaScript").skillCategory(SkillCategory.PROG_LANG).description("General-purpose programming languages").build();

        // DB
        Skill postgres = Skill.builder().name("PostgreSQL").skillCategory(SkillCategory.DB).description("Databases and data storage technologies").build();
        Skill mysql = Skill.builder().name("MySQL").skillCategory(SkillCategory.DB).description("Databases and data storage technologies").build();
        Skill mongodb = Skill.builder().name("MongoDB").skillCategory(SkillCategory.DB).description("Databases and data storage technologies").build();

        // DEVOPS
        Skill docker = Skill.builder().name("Docker").skillCategory(SkillCategory.DEVOPS).description("Tools and practices for deployment, CI/CD, and infrastructure").build();
        Skill kubernetes = Skill.builder().name("Kubernetes").skillCategory(SkillCategory.DEVOPS).description("Tools and practices for deployment, CI/CD, and infrastructure").build();
        Skill githubActions = Skill.builder().name("GitHub Actions").skillCategory(SkillCategory.DEVOPS).description("Tools and practices for deployment, CI/CD, and infrastructure").build();

        // FRONTEND
        Skill html = Skill.builder().name("HTML").skillCategory(SkillCategory.FRONTEND).description("Front-end and UI-related technologies").build();
        Skill css = Skill.builder().name("CSS").skillCategory(SkillCategory.FRONTEND).description("Front-end and UI-related technologies").build();
        Skill typescript = Skill.builder().name("TypeScript").skillCategory(SkillCategory.FRONTEND).description("Front-end and UI-related technologies").build();
        Skill vue = Skill.builder().name("Vue.js").skillCategory(SkillCategory.FRONTEND).description("Front-end and UI-related technologies").build();

        // TESTING
        Skill junit = Skill.builder().name("JUnit").skillCategory(SkillCategory.TESTING).description("Tools and frameworks for testing and QA").build();
        Skill cypress = Skill.builder().name("Cypress").skillCategory(SkillCategory.TESTING).description("Tools and frameworks for testing and QA").build();
        Skill jest = Skill.builder().name("Jest").skillCategory(SkillCategory.TESTING).description("Tools and frameworks for testing and QA").build();

        // DATA
        Skill pandas = Skill.builder().name("Pandas").skillCategory(SkillCategory.DATA).description("Data science, analytics, and machine learning tools").build();
        Skill tensorflow = Skill.builder().name("TensorFlow").skillCategory(SkillCategory.DATA).description("Data science, analytics, and machine learning tools").build();
        Skill powerbi = Skill.builder().name("Power BI").skillCategory(SkillCategory.DATA).description("Data science, analytics, and machine learning tools").build();

        // FRAMEWORK
        Skill springBoot = Skill.builder().name("Spring Boot").skillCategory(SkillCategory.FRAMEWORK).description("Application frameworks and libraries").build();
        Skill react = Skill.builder().name("React").skillCategory(SkillCategory.FRAMEWORK).description("Application frameworks and libraries").build();
        Skill angular = Skill.builder().name("Angular").skillCategory(SkillCategory.FRAMEWORK).description("Application frameworks and libraries").build();

        // --- Persistér alle skills ---
        em.persist(java);
        em.persist(python);
        em.persist(csharp);
        em.persist(javascript);
        em.persist(postgres);
        em.persist(mysql);
        em.persist(mongodb);
        em.persist(docker);
        em.persist(kubernetes);
        em.persist(githubActions);
        em.persist(html);
        em.persist(css);
        em.persist(typescript);
        em.persist(vue);
        em.persist(junit);
        em.persist(cypress);
        em.persist(jest);
        em.persist(pandas);
        em.persist(tensorflow);
        em.persist(powerbi);
        em.persist(springBoot);
        em.persist(react);
        em.persist(angular);

        // --- Link nogle skills til kandidater ---
        c1.addSkill(java);
        c1.addSkill(postgres);
        c1.addSkill(html);
        c1.addSkill(css);
        c1.addSkill(springBoot);

        c2.addSkill(python);
        c2.addSkill(docker);
        c2.addSkill(tensorflow);
        c2.addSkill(react);

        tx.commit();
        em.close();

        System.out.println("✅ Database populated with all skills and sample candidates!");
    }
}

