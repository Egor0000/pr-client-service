package md.utm.isa.pr.clientservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import md.utm.isa.pr.clientservice.dto.ClientOrderDto;
import md.utm.isa.pr.clientservice.dto.Food;
import md.utm.isa.pr.clientservice.dto.OrderDto;
import md.utm.isa.pr.clientservice.entity.Client;
import md.utm.isa.pr.clientservice.service.ClientService;
import md.utm.isa.pr.clientservice.service.RestaurantMenu;
import md.utm.isa.pr.clientservice.util.MenuUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private List<Food> menu;

    @Value("${food-ordering.address}")
    private String address;

    @Value("${food-ordering.port}")
    private Integer port;

    @Value("${client-service.clients}")
    private String clientCount;

    private final RestaurantMenu restaurantMenu;

    @Value("${client-service..time-unit}")
    private Long timeUnit;
    @Value("${client-service..time-duration}")
    private Long timeDuration;

    private String path = "/order/";
    private WebClient webClient;
    private List<Food> foods = new ArrayList<>();


    private ConcurrentMap<String, Thread> clientList = new ConcurrentHashMap<>();

    public ClientServiceImpl(RestaurantMenu restaurantMenu) {
        this.restaurantMenu = restaurantMenu;
    }

    @PostConstruct
    private void onInit() {
        try {
            URI uri = new URI("http", null, address, port, null, null, null);
            URL url = uri.toURL();

            webClient = WebClient.builder()
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(Collections.singletonMap("url", url))
                    .build();
            log.info("{}", foods);
            foods = MenuUtil.getMenu();

            startClients();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public String postOrder(ClientOrderDto order) {
        log.info("Sending order {}", order);
        if (webClient != null) {
            Mono<String> response = webClient.post()
                    .uri(String.format("%s:%s%s", address, port, path))
                    .body(BodyInserters.fromValue(order))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class);

            return response.block();
        }
        return null;
    }

    private void startClients() {
        for (int i = 0; i < Integer.parseInt(clientCount); i++) {
            Thread r = new Thread(new Client(this, restaurantMenu, i));
            clientList.put(String.format("Client_%s", i), r);
            r.start();
        }
    }
}
