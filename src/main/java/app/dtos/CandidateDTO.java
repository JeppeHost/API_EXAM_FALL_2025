package app.dtos;

import app.entities.Candidate;
import app.entities.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {

    private Integer id;

    private String name;

    private String phone;

    private String education;

    private Set<SkillDTO> skills;

    public CandidateDTO(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.phone = candidate.getPhone();
        this.education = candidate.getEducation();
    }

    public static List<CandidateDTO> toDTOList(List<Candidate> candidates) {
        return candidates.stream().map(CandidateDTO::new).toList();
    }
}
