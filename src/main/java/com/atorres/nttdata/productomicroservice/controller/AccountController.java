package com.atorres.nttdata.productomicroservice.controller;

import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestClientproduct;
import com.atorres.nttdata.productomicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/account")
@Slf4j
public class AccountController {
    @Autowired
    AccountService accountService;

    /**
     * Endpoint para obtener todas las cuentas de un cliente
     * @param id id del cliente
     * @return devuelve una lista de cuentas
     */
    @GetMapping("/client/{id}")
    public Flux<AccountDao> getAllAccountClient(@PathVariable String id){
        return accountService.getAllAccountsByClient(id);
    }

    /**
     * Endpoint para crear una cuenta para un cliente por su id y un RequestAccount
     * @param id id del cliente
     * @param requestAccount request con los datos de la cuenta
     * @return retorna la entidad relacion client-product
     */
    @PostMapping("/client/{id}")
    public Mono<ResponseEntity<ClientProductDao>> createAccount(@PathVariable String id, @RequestBody Mono<RequestAccount> requestAccount){
        return requestAccount.flatMap(account -> {
            return accountService.createAccount(id,account).map(p -> {
                log.info("Cuenta Creada con exito");
                return ResponseEntity
                        .created(URI.create("/account/".concat(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }

    @DeleteMapping("")
    public Flux<Void> deleteAccount(@RequestBody RequestClientproduct requestClientproduct){
        return accountService.delete(requestClientproduct);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<AccountDao>> updateAccount(@RequestBody Mono<RequestUpdateAccount> request){
        return request.flatMap(account -> {
            return accountService.update(account).map(p -> {
                log.info("Cuenta actualizada con exito");
                return ResponseEntity
                        .created(URI.create(""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }



}
