package com.atorres.nttdata.productomicroservice.client;

import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebClientMicroservice {
    WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8080/api/client")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
            .build();

    public Flux<ClientDao> getClients(){
        return client.get()
                .uri("/")
                .retrieve()
                .bodyToFlux(ClientDao.class);
    }

    public Mono<ClientDao> getClientById(String id){
        return client.get()
                .uri("/{id}",id)
                .retrieve()
                .bodyToFlux(ClientDao.class)
                .single();
    }
}
