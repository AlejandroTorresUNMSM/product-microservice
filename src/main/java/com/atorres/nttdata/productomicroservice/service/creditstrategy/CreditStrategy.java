package com.atorres.nttdata.productomicroservice.service.creditstrategy;

import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditStrategy {
    Mono<Boolean> verifyCredit(Flux<CreditDao> listCredit);
}
