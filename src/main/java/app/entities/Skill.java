package app.entities;

import app.dtos.SkillDTO;
import app.enums.SkillCategory;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private SkillCategory skillCategory;

    public Skill(SkillDTO skillDTO) {
        this.name = skillDTO.getName();
        this.description = skillDTO.getDescription();
        this.skillCategory = skillDTO.getSkillCategory();
    }
}
