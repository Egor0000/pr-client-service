package md.utm.isa.pr.clientservice.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseClientOrderDto {
    private Long clientId;
    private Long orderId;
    private List<ResponseOrderDto> orders = new ArrayList<>();
}
