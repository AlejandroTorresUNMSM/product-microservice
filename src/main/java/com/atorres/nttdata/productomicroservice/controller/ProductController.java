package com.atorres.nttdata.productomicroservice.controller;

import com.atorres.nttdata.productomicroservice.model.ProductPos;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import com.atorres.nttdata.productomicroservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController("/api/product")
@Slf4j
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public Flux<ProductDao> getProducts(){
        return productService.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<ProductDao>> createClient(@Valid @RequestBody Mono<ProductPos> clientMono) {
        return clientMono.flatMap(client -> {
            return productService.create(client).map(p -> {
                log.info("Cliente creado con éxito");
                return ResponseEntity
                        .created(URI.create("/api/product/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }

    @GetMapping("/getclients")
    public Flux<ProductDao> getClients(){
        return productService.findAll();
    }
}
