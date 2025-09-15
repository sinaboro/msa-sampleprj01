package com.example.ordersystem.ordering.service;

import com.example.ordersystem.ordering.dto.ProductDto;
import com.example.ordersystem.ordering.dto.ProductUpdateStockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//name은 eureka에 등록된 호출할 서비스이 이름
@FeignClient(name = "product-service")
public interface ProductFeign {

    /*
        @RequestHeader("X-User-Id") String userId
        이 코드는 지금은 생략해도 상관없다.
        apigateway -> ordering -> product로 이동 한다고 할때
        product에서 userid, role이 필요하다면 그 때 보내면 되는 코드이다.
        코드 전송시에는 이런 코드 형태로 전송하면 된다.
        또한, openfeign는 동기 통신에서 사용 된다.
     */
    @GetMapping("/product/{productId}")
    ProductDto getProductId(@PathVariable("productId") Long productId,
                            @RequestHeader("X-User-Id") String userId);

    @PutMapping("product/updatestock")
    void updateProductStock(@RequestBody ProductUpdateStockDto productUpdateStockDto);
}
