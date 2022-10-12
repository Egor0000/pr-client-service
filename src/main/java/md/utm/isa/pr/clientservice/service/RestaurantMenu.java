package md.utm.isa.pr.clientservice.service;


import md.utm.isa.pr.clientservice.dto.Food;

import java.util.List;

public interface RestaurantMenu {
    List<Food> getMenu();

    Food getMaxPreparationTime(List<Food> foods);

    List<Food> getRandomFoods(int nr);
}
