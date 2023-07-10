package com.atorres.nttdata.productomicroservice.service.strategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountStrategy {
    Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount);
}
