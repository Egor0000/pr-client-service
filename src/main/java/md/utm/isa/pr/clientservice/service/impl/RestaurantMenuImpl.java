package md.utm.isa.pr.clientservice.service.impl;

import lombok.RequiredArgsConstructor;
import md.utm.isa.pr.clientservice.dto.Food;
import md.utm.isa.pr.clientservice.dto.MenuDto;
import md.utm.isa.pr.clientservice.dto.RestaurantDto;
import md.utm.isa.pr.clientservice.service.RestaurantMenu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RestaurantMenuImpl implements RestaurantMenu {
    @Value("${food-ordering.address}")
    private String foodServiceAddress;

    @Value("${food-ordering.port}")
    private Integer foodOrderingPort;

    private WebClient webClient;

    private Map<Long, RestaurantDto> restaurants = new HashMap<>();

    @PostConstruct
    private void init() {
        webClient = WebClient.create();
    }

    @Override
    public MenuDto getMenu() {
        try {
            MenuDto menu =  webClient.get().uri(String.format("%s:%s%s", foodServiceAddress, foodOrderingPort, "/menu"))
                    .retrieve()
                    .bodyToMono(MenuDto.class)
                    .block();
            if (menu !=null) {
                updateRestaurantList(menu);
            }
            return menu;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Food getMaxPreparationTime(List<Food> foods) {
        return foods.stream()
                .max(Comparator.comparing(Food::getPreparationTime))
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Food> getRandomFoods(RestaurantDto restaurant, int nr) {
        List<Food> randomizedFoodList = new ArrayList<>();
        List<Food> menu = restaurant.getMenu();

        for (int i = 0; i < nr; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(nr) % menu.size();
            Food randomFood = menu.get(randomIndex);
            randomizedFoodList.add(randomFood);
        }

        return randomizedFoodList;
    }

    @Override
    public Map<Long, RestaurantDto> getRestaurants() {
        return restaurants;
    }

    private void updateRestaurantList(MenuDto menu) {
        for (RestaurantDto restaurant: menu.getRestaurantData()) {
            restaurants.put(restaurant.getRestaurantId(), restaurant);
        }
    }
}
