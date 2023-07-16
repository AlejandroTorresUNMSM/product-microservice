package com.atorres.nttdata.productomicroservice.service.creditstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BussinesCreditStrategy implements CreditStrategy{
    @Override
    public Mono<Boolean> verifyCredit(Flux<CreditDao> listCredit) {
        return listCredit
                .all(creditDao ->  creditDao.getBalance().doubleValue() <=10000)
                .flatMap(  exist -> exist ? Mono.just(Boolean.TRUE):Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El credito no debe pasar 10 000")));
    }
}
