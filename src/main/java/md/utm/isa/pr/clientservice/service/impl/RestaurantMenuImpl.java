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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RestaurantMenuImpl implements RestaurantMenu {
    @Value("${food-ordering.address}")
    private String foodServiceAddress;

    @Value("${food-ordering.port}")
    private Integer foodOrderingPort;

    private WebClient webClient;

    @PostConstruct
    private void init() {
        webClient = WebClient.create();
    }

    @Override
    public MenuDto getMenu() {
        try {
            return webClient.get().uri(String.format("%s:%s%s", foodServiceAddress, foodOrderingPort, "/menu"))
                    .retrieve()
                    .bodyToMono(MenuDto.class)
                    .block();
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
}
