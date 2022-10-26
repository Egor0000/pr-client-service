package md.utm.isa.pr.clientservice.service;


import md.utm.isa.pr.clientservice.dto.Food;
import md.utm.isa.pr.clientservice.dto.MenuDto;
import md.utm.isa.pr.clientservice.dto.RestaurantDto;

import java.util.List;

public interface RestaurantMenu {
    MenuDto getMenu();

    Food getMaxPreparationTime(List<Food> foods);

    List<Food> getRandomFoods(RestaurantDto restaurant, int nr);


}
