package com.example.ordersystem.ordering.service;

import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.dto.ProductDto;
import com.example.ordersystem.ordering.dto.ProductUpdateStockDto;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final RestTemplate restTemplate;
    private final ProductFeign productFeign;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderingService(OrderingRepository orderingRepository, RestTemplate restTemplate, ProductFeign productFeign, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderingRepository = orderingRepository;
        this.restTemplate = restTemplate;
        this.productFeign = productFeign;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Ordering orderCreate(OrderCreateDto orderDto, String userId) {

        //상품 조회 - 동기 통신
        //product get 요청
        String productGetUrl = "http://product-service/product/"+orderDto.getProductId();

        /*
          apigateway에서 X-User-Id, X-User-Role를 Custom-Header를 이용해서
          Ordering-service에 넘겨주었다. 지금은 당장 필요없지만
          만약 Ordering-service에서 전달 받은 헤더값을 product에 넘겨주기 위해서는
          HttpHeaders 추가해서 넘겨주어야 한다.
          넘겨줄 값이 없다면 null로 전달해도 상관없다.
          지금도 역시 null값을 전달해도 상관 없다.
         */
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-User-Id", userId);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        
        // ProductDto.class : 반환타입, httpEntity : 요청헤더
        ResponseEntity<ProductDto> response = restTemplate.exchange(productGetUrl, HttpMethod.GET, httpEntity, ProductDto.class);
        ProductDto productDto = response.getBody();
        int quantity = orderDto.getProductCount();
        if(productDto.getStockQuantity() < quantity){
            throw new IllegalArgumentException("재고 부족");
        }else {
            //상품 감소 -  동기, 비동기 통신
            //product put 요청
            String productPutUrl = "http://product-service/product/updatestock";
            //json값으로 전달하겠다.
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProductUpdateStockDto> updateEntity = new HttpEntity(
                ProductUpdateStockDto.builder()
                    .productId(orderDto.getProductId())
                        .productQuantity(orderDto.getProductCount())
                        .build(),
                    httpHeaders
            );

            restTemplate.exchange(productPutUrl, HttpMethod.PUT, updateEntity, Void.class);
        }

        Ordering ordering = Ordering.builder()
                .memberId(Long.parseLong(userId))
                .productId(orderDto.getProductId())
                .quantity(orderDto.getProductCount())
                .build();
        orderingRepository.save(ordering);
        return  ordering;
    }

    public Ordering orderFeignKafkaCreate(OrderCreateDto orderDto, String userId) {

        ProductDto productDto = productFeign.getProductId(orderDto.getProductId(), userId);

        int quantity = orderDto.getProductCount();
        if(productDto.getStockQuantity() < quantity){
            throw new IllegalArgumentException("재고 부족");
        }else {


            ProductUpdateStockDto dto =
                    ProductUpdateStockDto.builder()
                            .productId(orderDto.getProductId())
                            .productQuantity(orderDto.getProductCount())
                            .build();

            /* productFeign.updateProductStock( dto);*/
            kafkaTemplate.send("update-stock-topic", dto);
        }

        Ordering ordering = Ordering.builder()
                .memberId(Long.parseLong(userId))
                .productId(orderDto.getProductId())
                .quantity(orderDto.getProductCount())
                .build();
        orderingRepository.save(ordering);
        return  ordering;
    }

}
