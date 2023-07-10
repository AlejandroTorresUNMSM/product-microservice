package com.atorres.nttdata.productomicroservice.service.creditstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonalCreditStrategy implements CreditStrategy{
    @Override
    public Mono<Boolean> verifyCredit(Flux<CreditDao> listCredit) {
        return listCredit
                .single()
                .map(creditDao ->  creditDao.getBalance() <=1000)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND,"No cumple con el balance")))
                .onErrorResume( error ->Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Personal solo debe tener 1 credito")));
    }
}
