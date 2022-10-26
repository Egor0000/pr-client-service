package md.utm.isa.pr.clientservice.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.utm.isa.pr.clientservice.dto.*;
import md.utm.isa.pr.clientservice.service.ClientService;
import md.utm.isa.pr.clientservice.service.RestaurantMenu;
import md.utm.isa.pr.clientservice.util.OrderUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class Client implements Runnable {
    private final ClientService clientService;
    private final RestaurantMenu restaurantMenu;

    private final int clientId;
    @Override
    public void run() {
        while (true) {
            try {
                ClientOrderDto clientOrder = new ClientOrderDto();
                clientOrder.setClientId((long)clientId);

                for (int i =0; i<ThreadLocalRandom.current().nextInt(1, 5); i++) {
                    clientOrder.getOrders().add(generateOrder());
                }

                clientService.postOrder(clientOrder);
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500)* 100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private OrderDto generateOrder() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(OrderUtil.getNextOrderId());
        MenuDto menu = restaurantMenu.getMenu();

        RestaurantDto restaurant = menu.getRestaurantData().get(ThreadLocalRandom.current().nextInt(0, menu.getRestaurants().intValue()));
        orderDto.setRestaurantId(restaurant.getRestaurantId());

        List<Food> randomList = restaurantMenu.getRandomFoods(restaurant, ThreadLocalRandom.current().nextInt(1, 6));

        orderDto.setItems(randomList.stream()
                .map(Food::getId)
                .collect(Collectors.toList()));

        orderDto.setPriority(ThreadLocalRandom.current().nextInt(1, 6));

        orderDto.setMaxWait(restaurantMenu.getMaxPreparationTime(randomList).getPreparationTime()*1.8);
        orderDto.setCreatedTime(System.currentTimeMillis());

        return orderDto;
    }
}
