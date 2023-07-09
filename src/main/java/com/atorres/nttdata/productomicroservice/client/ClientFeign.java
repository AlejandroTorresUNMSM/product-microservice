package com.atorres.nttdata.productomicroservice.client;

import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "client-microservice", url = "${client.service.url}")
public interface ClientFeign {
    @GetMapping(value="")
    Flux<ClientDao> getAll();

    @GetMapping(value = "/{id}")
    Mono<ResponseEntity<ClientDao>> getMovie(@PathVariable String id);

}
