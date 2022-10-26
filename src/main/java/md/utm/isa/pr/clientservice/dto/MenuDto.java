package md.utm.isa.pr.clientservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDto {
    private Long restaurants;
    private List<RestaurantDto> restaurantData;
}
