package md.utm.isa.pr.clientservice.dto;

import lombok.Data;

@Data
public class ResponseOrderDto {
    private Long orderId;
    private Long restaurantId;
    private String restaurantAddress;
    private Long estimateWaitingTime;
    private Long createdTime;
    private Long registeredTime;
}
