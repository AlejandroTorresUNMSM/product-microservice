package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.utils.AccountCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountStrategy {
    Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount);
    Mono<Boolean> verifyClient(Flux<AccountDao> listaAccount, Mono<AccountCategory> accountCategory, Flux<CreditDao> listCredit);
}
