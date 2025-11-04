package app.entities;

import app.dtos.CandidateDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String phone;

    private String education;

    @ManyToMany
    @JoinTable(
            name = "candidate_skill",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    public Candidate(CandidateDTO candidateDTO){
        this.id = candidateDTO.getId();
        this.name = candidateDTO.getName();
        this.phone = candidateDTO.getPhone();
        this.education = candidateDTO.getEducation();
    }

    public void addSkill(Skill skill){
        this.skills.add(skill);
    }

    public void removeSkill(Skill skill){
        this.skills.remove(skill);
    }
}
