package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.repository.CreditRepository;
import com.atorres.nttdata.productomicroservice.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonalAccountStrategy implements AccountStrategy{
    @Autowired
    private CreditService creditService;

    public Mono<Boolean> verifyClient(Flux<AccountDao> listaAccount,Mono<Boolean> vip,String idClient){
        return vip.flatMap(isVip -> isVip ?  this.verifyVip(idClient) : Mono.just(true))
                .flatMap(band -> !band ? Mono.just(false) : verifyAccount(listaAccount));
    }

    public Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .groupBy(AccountDao::getType)
                .flatMap(group -> group.count().map(count -> Pair.of(group.key(), count)))
                .collectList()
                .map(groups -> groups.size() <= 3 && groups.size() >= 1 && groups.stream().allMatch(pair -> pair.getSecond() == 1))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se cumplen las condiciones requeridas para los tipos de cuenta personal")));
    }

    public Mono<Boolean> verifyVip(String idClient){
        return creditService.getAllCreditByClient(idClient).any(credit -> true)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente tiene que tener al menos un credito")));
    }


}
