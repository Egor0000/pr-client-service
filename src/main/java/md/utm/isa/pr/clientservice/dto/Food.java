package md.utm.isa.pr.clientservice.dto;

import lombok.Data;
import md.utm.isa.pr.clientservice.enums.CookingApparatus;

@Data
public class Food {
    private Long id;
    private String name;
    private Long preparationTime;
    private Long complexity;
    private CookingApparatus cookingApparatus;
}
