package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.utils.AccountCategory;
import com.atorres.nttdata.productomicroservice.utils.AccountType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BussinesAccountStrategy implements  AccountStrategy{
    /**
     * Metodo que verifica si el cliente cumple como bussines
     * @param listaAccount lista cuentas
     * @param accountCategory categoria cuenta ingresada
     * @param listCredit lista creditos
     * @return boolean
     */
    @Override
    public Mono<Boolean> verifyClient(Flux<AccountDao> listaAccount, Mono<AccountCategory> accountCategory, Flux<CreditDao> listCredit){
        return accountCategory.filter(enumValue -> enumValue.equals(AccountCategory.mype) || enumValue.equals(AccountCategory.normal))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"Las cuentas bussines no pueden ser VIP")))
                .flatMap(enumValue -> enumValue.equals(AccountCategory.mype)? verifyMype(listaAccount,listCredit) : Mono.just(true))
                .single()
                .flatMap(band -> Boolean.FALSE.equals(band) ? Mono.just(false) : verifyAccount(listaAccount));
    }

    /**
     * Metodo que verifica las cuentas bussines
     * @param listAccount lista cuentas
     * @return boolean
     */
    @Override
    public Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .all(product -> product.getType().equals(AccountType.CC) && product.getBalance().doubleValue()>=0)
                .flatMap(exist -> exist.equals(Boolean.FALSE) ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Los clientes bussines solo pueden tener cuentas CC")) : Mono.just(Boolean.TRUE));
    }

    /**
     * Metodo que verifica si cumple para un producto mype
     * @param listAccount lista cuentas
     * @param listCredit lisa creditos
     * @return boolean
     */
    public Mono<Boolean> verifyMype(Flux<AccountDao> listAccount,Flux<CreditDao> listCredit) {
        return listCredit.any(credit -> true)
                .flatMap(ac -> Boolean.TRUE.equals(ac) ? verifyMypeAccount(listAccount) : Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente MYPE tiene que tener al menos un credito")));
    }

    /**
     * Metodo que verifica si las cuentas califican para un producto mype
     * @param listAccount lista cuentas
     * @return boolean
     */
    public Mono<Boolean> verifyMypeAccount(Flux<AccountDao> listAccount){
        return listAccount
                .filter(account -> account.getAccountCategory().equals(AccountCategory.normal) && account.getType().equals(AccountType.CC))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente MYPE tiene que tener al menos una cuenta CC NORMAL")))
                .hasElements();
    }
}
