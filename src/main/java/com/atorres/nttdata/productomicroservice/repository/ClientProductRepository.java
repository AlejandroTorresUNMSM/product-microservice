package com.atorres.nttdata.productomicroservice.repository;

import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClientProductRepository extends ReactiveMongoRepository<ClientProductDao,String> {

    Flux<ClientProductDao> findByClient(String client);
}
