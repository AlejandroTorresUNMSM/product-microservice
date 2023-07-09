package com.atorres.nttdata.productomicroservice.client;

import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebClientMicroservice {
    WebClient client = WebClient.create("http://localhost:8080/api/client");

    public Flux<ClientDao> getClients(){
        return client.get()
                .uri("")
                .retrieve()
                .bodyToFlux(ClientDao.class);
    }

    public Mono<ClientDao> getClientById(String id){
        return client.get()
                .uri("/{id}",id)
                .retrieve()
                .bodyToMono(ClientDao.class);
    }
}
