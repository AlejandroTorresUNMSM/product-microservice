package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountStrategy {
    Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount);
}
