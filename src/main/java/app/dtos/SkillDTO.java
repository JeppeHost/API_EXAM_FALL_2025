package app.dtos;

import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class SkillDTO {

    private Integer id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private SkillCategory skillCategory;

    public SkillDTO(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.description = skill.getDescription();
        this.skillCategory = skill.getSkillCategory();
    }

    public static List<SkillDTO> toDTOList(List<Skill> skills) {
        return skills.stream().map(SkillDTO::new).toList();
    }
}
