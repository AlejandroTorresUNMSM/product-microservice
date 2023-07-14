package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.service.CreditService;
import com.atorres.nttdata.productomicroservice.utils.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BussinesAccountStrategy implements  AccountStrategy{
    @Autowired
    private CreditService creditService;

    public Mono<Boolean> verifyClient(Flux<AccountDao> listaAccount,Mono<Boolean> vip,String idClient){
        return vip.flatMap(isVip -> isVip ?  this.verifyVip(idClient) : Mono.just(true))
                .flatMap(band -> !band ? Mono.just(false) : verifyAccount(listaAccount));
    }
    @Override
    public Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .all(product -> product.getType().equals(AccountType.CC) && product.getBalance()>=0)
                .flatMap(exist -> exist.equals(Boolean.FALSE) ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se cumplen las requisitos para cuentas bussines")) : Mono.just(Boolean.TRUE));
    }

    public Mono<Boolean> verifyVip(String idClient){
        return creditService.getAllCreditByClient(idClient).any(credit -> true)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente tiene que tener al menos un credito")));
    }
}
