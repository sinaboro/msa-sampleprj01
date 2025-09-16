package com.example.ordersystem.product.controller;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.dto.ProductResDto;
import com.example.ordersystem.product.dto.ProductUpdateStockDto;
import com.example.ordersystem.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
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
    public ResponseEntity<?> productCreate(ProductRegisterDto dto,
                                           @RequestHeader("X-User-Id") String userId) {
        Product product = productService.productCreate(dto, userId);
        return new ResponseEntity<>(product.getId(), HttpStatus.CREATED);
    }

    // 상품 갯수 조회
    @GetMapping("{id}")
    public ResponseEntity<?> productDetail(@PathVariable("id") Long id,
                                           @RequestHeader("X-User-Id") String userId) throws InterruptedException {
//        Thread.sleep(3000L);
        ProductResDto productResDto = productService.productDetail(id);
        return new ResponseEntity<>(productResDto, HttpStatus.OK);
    }

    //상품 수량 감소
    @PutMapping("/updatestock")
    public ResponseEntity updateStock(@RequestBody ProductUpdateStockDto productUpdateStockDto) {
        Product product =  productService.updateStockQuantity(productUpdateStockDto);
        return new ResponseEntity<>(product.getId(), HttpStatus.OK);
    }

}
