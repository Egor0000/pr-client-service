package md.utm.isa.pr.clientservice.service;

import md.utm.isa.pr.clientservice.dto.*;
import md.utm.isa.pr.clientservice.util.OrderUtil;

public interface ClientService {
    ResponseClientOrderDto postOrder(ClientOrderDto orderDto);

    void postRating(RestaurantRating rating);

    PreparedOrderDto getPreparedOrder(ResponseOrderDto id);

    void createNextThread(String name);
}
