package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.repository.CreditRepository;
import com.atorres.nttdata.productomicroservice.service.CreditService;
import com.atorres.nttdata.productomicroservice.utils.AccountCategory;
import com.atorres.nttdata.productomicroservice.utils.AccountType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PersonalAccountStrategy implements AccountStrategy{
    /**
     * Metodo que verifica si el cliente cumple como personal
     * @param listaAccount lista cuentas del cliente
     * @param accountCategory categoria de la cuenta ingresada
     * @param listCredit lista de creditos
     * @return boolean
     */
    @Override
    public Mono<Boolean> verifyClient(Flux<AccountDao> listaAccount, Mono<AccountCategory> accountCategory,Flux<CreditDao> listCredit){
        return accountCategory.filter(enumValue -> enumValue.equals(AccountCategory.vip) || enumValue.equals(AccountCategory.normal))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"Las cuentas personales no pueden ser MYPE")))
                .flatMap(enumValue -> enumValue.equals(AccountCategory.vip)? verifyVip(listaAccount,listCredit) : Mono.just(true))
                .single()
                .flatMap(band -> Boolean.FALSE.equals(band) ? Mono.just(false) : verifyAccount(listaAccount))
                .doOnNext(value -> log.info("verifyClient: "+value.toString()));
    }

    /**
     * Metodo que verifica que las cuenta personal
     * @param listAccount lista cuentas
     * @return boolean
     */
    @Override
    public Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .groupBy(AccountDao::getType)
                .flatMap(group -> group.count().map(count -> Pair.of(group.key(), count)))
                .collectList()
                .map(groups -> groups.size() <= 3 && !groups.isEmpty() && groups.stream().allMatch(pair -> pair.getSecond() == 1))
                .flatMap(value -> Boolean.TRUE.equals(value) ? Mono.just(true) : Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Cliente personal solo puede tener 1 cuenta de cada una")));
    }

    /**
     * Metodo que verifica si el cliente puede crear un producto vip , primero verifica si linea de credito
     * @param listAccount lista cuentas
     * @param listCredit lista creditos
     * @return boolean
     */
    public Mono<Boolean> verifyVip(Flux<AccountDao> listAccount,Flux<CreditDao> listCredit){
        return listCredit.any(credit -> true)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente VIP debe tener al menos un credito")))
                .flatMap(ac -> Boolean.FALSE.equals(ac) ? this.verifyVipAccount(listAccount) : Mono.just(false))
                .doOnNext(value -> log.info("verifyVip: "+value.toString()));
    }

    /**
     * Metodo que verifica si sus cuentas califican para un producto vip
     * @param listAccount lista cuentas
     * @return boolean
     */
    public Mono<Boolean> verifyVipAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .filter(account -> account.getBalance().doubleValue() >=500 && account.getType().equals(AccountType.CA))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente VIP debe tener una cuenta CA con minimo 500 soles")))
                .doOnNext(value -> log.info("verifyVipAccount: "+value.toString()))
                .hasElements();
    }


}
