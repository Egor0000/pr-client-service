package md.utm.isa.pr.clientservice.service;

import md.utm.isa.pr.clientservice.dto.ClientOrderDto;
import md.utm.isa.pr.clientservice.dto.OrderDto;
import md.utm.isa.pr.clientservice.util.OrderUtil;

public interface ClientService {
    String postOrder(ClientOrderDto orderDto);
}
