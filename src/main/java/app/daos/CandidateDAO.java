package app.daos;

import app.dtos.CandidateDTO;
import app.entities.Candidate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CandidateDAO implements IDAO<CandidateDTO, Integer> {

    private final EntityManagerFactory emf;

    public CandidateDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public CandidateDTO read(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            Candidate candidate = em.find(Candidate.class, id);
            return new CandidateDTO(candidate);
        }
    }

    @Override
    public List<CandidateDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Candidate> query = em.createQuery("SELECT c FROM Candidate c", Candidate.class);
            return CandidateDTO.toDTOList(query.getResultList());
        }
    }

    @Override
    public CandidateDTO create(CandidateDTO candidateDTO) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Candidate candidate = new Candidate(candidateDTO);
            em.persist(candidate);
            em.getTransaction().commit();
            return new CandidateDTO(candidate);
        }
    }

    @Override
    public CandidateDTO update(Integer id, CandidateDTO candidateDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate c = em.find(Candidate.class, id);
            c.setName(candidateDTO.getName());
            c.setPhone(candidateDTO.getPhone());
            c.setEducation(candidateDTO.getEducation());
            //c.setSkills(candidateDTO.getSkills());
            Candidate mergedCandidate = em.merge(c);
            em.getTransaction().commit();
            return mergedCandidate != null ? new CandidateDTO(mergedCandidate) : null;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate != null){
                em.remove(candidate);
            }
            em.getTransaction().commit();
        }
    }


    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, integer);
            return candidate != null;
        }
    }
}
