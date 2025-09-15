package com.example.ordersystem.product.service;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.dto.ProductResDto;
import com.example.ordersystem.product.dto.ProductUpdateStockDto;
import com.example.ordersystem.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product productCreate(ProductRegisterDto dto, String userId){

        Product product = productRepository.save(dto.toEntity(Long.parseLong(userId)));
        return product;
    }

    public ProductResDto productDetail(Long id){
        Product product = productRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("없는 상품입니다."));

        return ProductResDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }

    public Product updateStockQuantity(ProductUpdateStockDto productUpdateStockDto) {

        Product product = productRepository.findById(productUpdateStockDto.getProductId()).
                orElseThrow(() -> new EntityNotFoundException("없는 상품입니다."));

        product.updateStockQuantity(productUpdateStockDto.getProductQuantity());
        return product;
    }

    /* KafkaConsumerConfig.java ->  public ConcurrentKafkaListenerContainerFactory<String, String>
        kafkaListener() : 메소드명 기입
    */
    @KafkaListener(topics = "update-stock-topic", containerFactory = "kafkaListener")
    public void stockConsumer(String message){
        System.out.println(message);

        ObjectMapper objectMapper = new ObjectMapper();

        ProductUpdateStockDto dto = null;
        try{
           dto = objectMapper.readValue(message, ProductUpdateStockDto.class);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
        this.updateStockQuantity(dto);
    }
}
