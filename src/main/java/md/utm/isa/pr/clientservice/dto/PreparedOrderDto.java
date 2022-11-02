package md.utm.isa.pr.clientservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreparedOrderDto {
    private Long orderId;
    private Long tableId;
    private Long waiterId;
    private List<Long> items;
    private Integer priority;
    private Double maxWait;
    private Long pickUpTime;
    private Long cookingTime;
    private List<CookingDetailDto> cookingDetails;
    private int rating;
    private boolean isExternal;
    private Long restaurantId;
}
