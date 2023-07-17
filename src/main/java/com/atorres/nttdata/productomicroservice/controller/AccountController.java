package com.atorres.nttdata.productomicroservice.controller;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestClientproduct;
import com.atorres.nttdata.productomicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
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
     * Metodo para traer la cuenta
     * @param productId id producto
     * @return cuenta
     */
    @GetMapping(value = "/{productId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<AccountDao> getAccount(
            @PathVariable String productId){
        return accountService.getAccount(productId)
                .doOnNext(account -> log.info("Cuenta encontrada con exito"));
    }

    /**
     * Endpoint para obtener todas las cuentas de un cliente
     * @param id id del cliente
     * @return devuelve una lista de cuentas
     */
    @GetMapping(value = "/client/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountDao> getAllAccountClient(@PathVariable String id){
        return accountService.getAllAccountsByClient(id)
                .doOnNext(account -> log.info("Cuenta encontrada: "+account.getId()));
    }

    /**
     * Endpoint para crear una cuenta para un cliente por su id y un RequestAccount
     * @param id id del cliente
     * @param requestAccount request con los datos de la cuenta
     * @return retorna la entidad relacion client-product
     */
    @PostMapping(value = "/client/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ClientProductDao> createAccount(@PathVariable String id, @RequestBody Mono<RequestAccount> requestAccount){
        return requestAccount.flatMap(account -> accountService.createAccount(id,account)
                    .doOnSuccess(v -> log.info("Cuenta creada con exito")));
    }

    /**
     * Metodo para eliminar una cuenta
     * @param requestClientproduct request
     * @return void
     */
    @DeleteMapping(value ="/delete",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Void> deleteAccount(@RequestBody RequestClientproduct requestClientproduct){
        return accountService.delete(requestClientproduct)
                .doOnNext(v -> log.info("Cuenta eliminada con exito"));
    }

    /**
     * Metodo para actualizar el balance de la cuenta
     * @param request request
     * @return AccountDao
     */
    @PutMapping(value="/update",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<AccountDao> updateAccount(@RequestBody Mono<RequestUpdateAccount> request){
        return request.flatMap(account -> accountService.update(account)
                    .doOnSuccess(v -> log.info("Cuenta actualizada con exito")));
    }



}
