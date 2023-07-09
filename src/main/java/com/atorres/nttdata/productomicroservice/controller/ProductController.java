package com.atorres.nttdata.productomicroservice.controller;

import com.atorres.nttdata.productomicroservice.model.ProductPos;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import com.atorres.nttdata.productomicroservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/product")
@Slf4j
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public Flux<ProductDao> getProducts(){
        return productService.findAll();
    }


    @PostMapping("/create/personal")
    public Mono<ResponseEntity<ProductDao>> createPersonal(@RequestBody Mono<ProductPos> productPosMono) {
        return productPosMono.flatMap(product -> {
            return productService.createPersonal(product).map(p -> {
                log.info("Producto Creado con exito");
                return ResponseEntity
                        .created(URI.create("/api/product/create/personal"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }
    @PostMapping("/create/bussines")
    public Mono<ResponseEntity<ProductDao>> createBussines(@RequestBody Mono<ProductPos> productPosMono) {
        return productPosMono.flatMap(product -> {
            return productService.createBussines(product).map(p -> {
                log.info("Producto Creado con exito");
                return ResponseEntity
                        .created(URI.create("/api/product/create/bussines"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }
}
