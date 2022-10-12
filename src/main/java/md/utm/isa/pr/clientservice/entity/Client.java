package md.utm.isa.pr.clientservice.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.utm.isa.pr.clientservice.dto.ClientOrderDto;
import md.utm.isa.pr.clientservice.dto.Food;
import md.utm.isa.pr.clientservice.dto.OrderDto;
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
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50)* 100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private OrderDto generateOrder() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(OrderUtil.getNextOrderId());
        orderDto.setRestaurantId(ThreadLocalRandom.current().nextLong(0, 2));

        //todo what is the max number of items?
        List<Food> randomList = restaurantMenu.getRandomFoods(ThreadLocalRandom.current().nextInt(1, 6));

        orderDto.setItems(randomList.stream()
                .map(Food::getId)
                .collect(Collectors.toList()));

        orderDto.setPriority(ThreadLocalRandom.current().nextInt(1, 6));

        orderDto.setMaxWait(restaurantMenu.getMaxPreparationTime(randomList).getPreparationTime()*1.3);
        orderDto.setCreatedTime(System.currentTimeMillis());

        return orderDto;
    }
}
