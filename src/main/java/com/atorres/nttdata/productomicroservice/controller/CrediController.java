package com.atorres.nttdata.productomicroservice.controller;

import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestClientproduct;
import com.atorres.nttdata.productomicroservice.model.RequestCredit;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/credit")
@Slf4j
public class CrediController {
    @Autowired
    CreditService creditService;

    @GetMapping("/client/{id}")
    public Flux<CreditDao> getAllCreditClient(@PathVariable String id){
        return creditService.getAllCreditByClient(id);
    }

    @PostMapping("/client/{id}")
    public Mono<ResponseEntity<ClientProductDao>> createCredit(@PathVariable String id, @RequestBody Mono<RequestCredit> requestCredit){
        return requestCredit.flatMap(credit -> {
            return creditService.createCredit(id,credit).map(p -> {
                log.info("Credito Creada con exito");
                return ResponseEntity
                        .created(URI.create("/credit/".concat(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p);
            });
        });
    }

    @DeleteMapping("")
    public Flux<Void> deleteCredit(@RequestBody RequestClientproduct request){
        return creditService.delete(request);
    }
}
