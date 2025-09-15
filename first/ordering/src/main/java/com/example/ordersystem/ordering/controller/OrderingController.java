package com.example.ordersystem.ordering.controller;

import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.service.OrderingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    /*
     --------- apigateway ---------------
     ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Id", userId)
                            .header("X-User-Role", "ROLE_" + role) // 역할 추가
                    )
                    .build();
     */
    @PostMapping("/create")
    public ResponseEntity<?> orderCreate(@RequestBody OrderCreateDto dtos,
                                         @RequestHeader("X-User-Id") String userId) {
        System.out.println("---------------orderCreate-------------------------");
        Ordering ordering = orderingService.orderCreate(dtos, userId);
        return new ResponseEntity<>(ordering.getId(), HttpStatus.CREATED);
    }

}
