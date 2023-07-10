package com.atorres.nttdata.productomicroservice.controller;

import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestProductPersonal;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import com.atorres.nttdata.productomicroservice.service.AccountService;
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
    @Autowired
    AccountService accountService;

    @GetMapping
    public Flux<ProductDao> getProducts(){
        return productService.findAll();
    }


    @PostMapping("/create/personal")
    public Mono<ResponseEntity<ProductDao>> createPersonal(@Valid @RequestBody Mono<RequestProductPersonal> requestProductPersonal) {
        return requestProductPersonal.flatMap(product -> {
            return productService.createProductPersonal(product).map(p -> {
                log.info("Producto Creado con exito");
                return ResponseEntity
                        .created(URI.create("/api/product/create/personal"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }

    /**
     * Metodo para crear una cuenta para un cliente por su id
     * @param clientId id del cliente
     * @param requestAccount request con los datos de la cuenta
     * @return retorna la entidad relacion client-product
     */
    @PostMapping("/account/{clientId}")
    public Mono<ResponseEntity<ClientProductDao>> createAccount(@PathVariable String clientId, @RequestBody Mono<RequestAccount> requestAccount){
        return requestAccount.flatMap(account -> {
            return accountService.createAccount(clientId,account).map(p -> {
                log.info("Cuenta Creada con exito");
                return ResponseEntity
                        .created(URI.create("/account/".concat(clientId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }

    /**
     * Funcion para obtener todas las cuentas de un cliente
     * @param id id del cliente
     * @return devuelve una lista de cuentas
     */
    @GetMapping("/account/client/{id}")
    public Flux<AccountDao> getAllAccountClient(@PathVariable String id){
        return accountService.getAllAccountsByClient(id);
    }

}
