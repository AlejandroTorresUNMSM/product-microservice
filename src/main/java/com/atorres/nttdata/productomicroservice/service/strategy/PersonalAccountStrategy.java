package com.atorres.nttdata.productomicroservice.service.strategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonalAccountStrategy implements AccountStrategy{
    public Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .groupBy(AccountDao::getType)
                .flatMap(group -> group.count().map(count -> Pair.of(group.key(), count)))
                .collectList()
                .map(groups -> groups.size() <= 3 && groups.size() >= 1 && groups.stream().allMatch(pair -> pair.getSecond() == 1))
                .filter(result -> result)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se cumplen las condiciones requeridas para los tipos de cuenta personal")));
    }
}
