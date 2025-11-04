package app.controllers;

import app.config.HibernateConfig;
import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.dtos.CandidateDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class CandidateController implements IController<CandidateDTO, Integer> {

    private final CandidateDAO candidateDAO;

    public CandidateController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.candidateDAO = new CandidateDAO(emf);
    }


    @Override
    public void read(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        CandidateDTO candidateDTO = candidateDAO.read(id);
        if (candidateDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Candidate was not found");
            return;
        }
        ctx.status(HttpStatus.OK);
        ctx.json(candidateDTO);

    }

    @Override
    public void readAll(Context ctx) {

        List<CandidateDTO> allCandidates = candidateDAO.readAll();
        if (!allCandidates.isEmpty()) {
            ctx.status(HttpStatus.OK).json(allCandidates);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Error retrieving candidates");
        }
    }

    @Override
    public void create(Context ctx) {

        CandidateDTO jsonRequest = ctx.bodyAsClass(CandidateDTO.class);

        CandidateDTO createdCandidateDTO = candidateDAO.create(jsonRequest);

        if (createdCandidateDTO == null) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error creating candidate");
        }
        ctx.status(HttpStatus.OK);
        ctx.json(createdCandidateDTO);

    }

    @Override
    public void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        CandidateDTO candidateDTO = new CandidateDTO(ctx.bodyAsClass(app.entities.Candidate.class));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id");
            return;
        }
        CandidateDTO updatedCandidateDTO = candidateDAO.update(id, candidateDTO);
        if (updatedCandidateDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Candidate not found");
            return;
        }
        ctx.status(HttpStatus.OK).json(updatedCandidateDTO);
    }


    @Override
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (!validatePrimaryKey(id)) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not a valid id, candidate could not be deleted");
            return;
        }
        candidateDAO.delete(id);
        ctx.status(HttpStatus.NO_CONTENT).result("Candidate with id: " + id + " deleted");
    }


    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0 && candidateDAO.validatePrimaryKey(id);
    }
}

