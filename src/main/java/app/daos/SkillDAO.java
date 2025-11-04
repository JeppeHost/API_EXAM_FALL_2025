package app.daos;

import app.dtos.CandidateDTO;
import app.dtos.SkillDTO;
import app.entities.Candidate;
import app.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SkillDAO implements IDAO<SkillDTO, Integer> {

    private final EntityManagerFactory emf;

    public SkillDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public SkillDTO read(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            Skill skill = em.find(Skill.class, id);
            return new SkillDTO(skill);
        }
    }

    @Override
    public List<SkillDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Skill> query = em.createQuery("SELECT s FROM Skill s", Skill.class);
            return SkillDTO.toDTOList(query.getResultList());
        }
    }

    @Override
    public SkillDTO create(SkillDTO skillDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = new Skill(skillDTO);
            em.persist(skill);
            em.getTransaction().commit();
            return new SkillDTO(skill);
        }
    }

    @Override
    public SkillDTO update(Integer id, SkillDTO skillDTO) {
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                Skill s = em.find(Skill.class, id);
                s.setName(skillDTO.getName());
                s.setDescription(skillDTO.getDescription());
                s.setSkillCategory(skillDTO.getSkillCategory());
                Skill mergedSkill = em.merge(s);
                em.getTransaction().commit();
                return mergedSkill != null ? new SkillDTO(mergedSkill) : null;
            }
        }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, id);
            if (skill != null){
                em.remove(skill);
            }
            em.getTransaction().commit();
        }
    }

    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill skill = em.find(Skill.class, id);
            return skill != null;
        }
    }
}
