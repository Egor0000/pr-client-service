package md.utm.isa.pr.clientservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantRating {
    private Long clientId;
    private Long orderId;
    private Long restaurantId;
    private List<PreparedOrderDto> orders;
}