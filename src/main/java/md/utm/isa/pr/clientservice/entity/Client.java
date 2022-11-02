package md.utm.isa.pr.clientservice.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.utm.isa.pr.clientservice.dto.*;
import md.utm.isa.pr.clientservice.service.ClientService;
import md.utm.isa.pr.clientservice.service.RestaurantMenu;
import md.utm.isa.pr.clientservice.util.OrderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class Client implements Runnable {
    private final ClientService clientService;
    private final RestaurantMenu restaurantMenu;

    private final String name;

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

                ResponseClientOrderDto responseClientOrderDto = clientService.postOrder(clientOrder);
                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(responseClientOrderDto.getOrders().size());

                ConcurrentHashMap<Long, PreparedOrderDto> preparedMap = new ConcurrentHashMap<>();

                List<Future<?>> futures = new ArrayList<>();

                for (ResponseOrderDto order: responseClientOrderDto.getOrders()) {
                    final CompletableFuture<ScheduledFuture<?>> cancellablePeriodicTask = new CompletableFuture<>();
                    final ScheduledFuture<?> cancellable = scheduler.scheduleAtFixedRate(() -> {

                         PreparedOrderDto preparedOrderDto = runRequest(order, cancellablePeriodicTask);
                         if (preparedOrderDto != null) {
                             preparedMap.put(preparedOrderDto.getOrderId(), preparedOrderDto);
                         }
                        },
                            Math.round(order.getEstimateWaitingTime()*1.8*100), Math.round(order.getEstimateWaitingTime()*1.8*100), TimeUnit.MILLISECONDS);
                    futures.add(cancellable);
                    cancellablePeriodicTask.complete(cancellable);


                    //                    futures.add(scheduler.scheduleAtFixedRate(() -> {
//                        PreparedOrderDto preparedOrderDto = null;
//
//                        preparedOrderDto =  getPreparedOrder(order);
//
//
//                        if (preparedOrderDto !=null) {
//                            preparedMap.put(preparedOrderDto.getOrderId(), preparedOrderDto);
//
//                            log.warn("PREPARED Order {}", order);
//                        } else {
//                            log.warn("Order {} not yet prepared", order);
//                        }
//                        return preparedOrderDto;
//
//                        //todo how much should client wait to take order? MAX wait time * 1.8 OR Estimated time?
//                    }, Math.round(order.getEstimateWaitingTime()*5.8*100), TimeUnit.MILLISECONDS));
                }

                for(Future<?> future: futures){
                    try {
                        future.get();
                    } catch (Exception ex) {
                    }
                }

                futures.clear();
                if (preparedMap.size() == responseClientOrderDto.getOrders().size()) {

                    RestaurantRating rating = new RestaurantRating();
                    rating.setClientId(responseClientOrderDto.getClientId());
                    rating.setOrderId(responseClientOrderDto.getOrderId());
                    rating.setOrders(new ArrayList<>(preparedMap.values()));

                    clientService.postRating(rating);
                    clientService.createNextThread(name);
                    break;
                } else {
                    log.error("FALSE. {} {}", responseClientOrderDto, preparedMap.values());
                }

                Thread.sleep(50);
            } catch (Exception e) {
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

    private PreparedOrderDto runRequest(ResponseOrderDto order, CompletableFuture<ScheduledFuture<?>> cancellable) {
        PreparedOrderDto preparedOrderDto = getPreparedOrder(order);

        if (preparedOrderDto != null) {

            Double prepTime = (double) (System.currentTimeMillis() - preparedOrderDto.getPickUpTime());
            Double maxWait = preparedOrderDto.getMaxWait()*1.8*1.4*100;

            Double ratio = prepTime / maxWait;

            int rating = ratingToStar(ratio);

            preparedOrderDto.setRating(rating);
            preparedOrderDto.setRestaurantId(order.getRestaurantId());

            log.info("RECEIVED NON NULL: Time: {} {}", prepTime, rating);
            cancellable.whenComplete((scheduledFuture, throwable) -> {
                if (throwable == null) {
                    scheduledFuture.cancel(true);
                }
            });
            return preparedOrderDto;
        }
        return null;
    }

    private PreparedOrderDto getPreparedOrder(ResponseOrderDto order) {
        return clientService.getPreparedOrder(order);
    }

    public int ratingToStar(double rating) {
        if (rating < 1) {
            return 5;
        } else if (rating < 1.1) {
            return 4;
        } else if (rating < 1.2) {
            return 3;
        } else if (rating < 1.3) {
            return 2;
        } else if (rating < 1.4) {
            return 1;
        }
        return 0;
    }
}
