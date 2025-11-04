package app.controllers;

import app.config.HibernateConfig;
import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.dtos.CandidateDTO;
import app.dtos.SkillDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class SkillController implements IController<SkillDTO, Integer> {

    private SkillDAO skillDAO;

    public SkillController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.skillDAO = new SkillDAO(emf);
    }


    @Override
    public void read(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
            SkillDTO skillDTO = skillDAO.read(id);
            if (skillDTO == null) {
                ctx.status(HttpStatus.NOT_FOUND).result("Skill was not found");
                return;
            }
            ctx.status(HttpStatus.OK);
            ctx.json(skillDTO);

    }


    @Override
    public void readAll(Context ctx) {
        List<SkillDTO> allSkills = skillDAO.readAll();
        if (!allSkills.isEmpty()) {
            ctx.status(HttpStatus.OK).json(allSkills);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Error retrieving skills");
        }
    }

    @Override
    public void create(Context ctx) {
        SkillDTO jsonRequest = ctx.bodyAsClass(SkillDTO.class);

        SkillDTO createdSkillDTO = skillDAO.create(jsonRequest);

        if (createdSkillDTO == null) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error creating skill");
        }
        ctx.status(HttpStatus.OK);
        ctx.json(createdSkillDTO);

    }

    @Override
    public void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        SkillDTO skillDTO = new SkillDTO(ctx.bodyAsClass(app.entities.Skill.class));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id");
            return;
        }
        SkillDTO updatedSkillDTO = skillDAO.update(id, skillDTO);
        if (updatedSkillDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Skill not found");
            return;
        }
        ctx.status(HttpStatus.OK).json(updatedSkillDTO);
    }


    @Override
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id, skill could not be deleted");
            return;
        }
        skillDAO.delete(id);
        ctx.status(HttpStatus.NO_CONTENT).result("Skill with id: " + id + " deleted");
    }

    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0 && skillDAO.validatePrimaryKey(id);
    }
}
