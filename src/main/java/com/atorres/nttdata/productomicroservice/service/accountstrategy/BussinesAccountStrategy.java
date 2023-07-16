package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.service.CreditService;
import com.atorres.nttdata.productomicroservice.utils.AccountCategory;
import com.atorres.nttdata.productomicroservice.utils.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BussinesAccountStrategy implements  AccountStrategy{

    @Override
    public Mono<Boolean> verifyClient(Flux<AccountDao> listaAccount, Mono<AccountCategory> accountCategory, String idClient, Flux<CreditDao> listCredit){
        return accountCategory.filter(enumValue -> enumValue.equals(AccountCategory.mype) || enumValue.equals(AccountCategory.normal))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"Las cuentas bussines no pueden ser VIP")))
                .flatMap(enumValue -> enumValue.equals(AccountCategory.mype)? verifyMype(idClient,listaAccount,listCredit) : Mono.just(true))
                .single()
                .flatMap(band -> !band ? Mono.just(false) : verifyAccount(listaAccount));
    }
    @Override
    public Mono<Boolean> verifyAccount(Flux<AccountDao> listAccount) {
        return listAccount
                .all(product -> product.getType().equals(AccountType.CC) && product.getBalance().doubleValue()>=0)
                .flatMap(exist -> exist.equals(Boolean.FALSE) ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Los clientes bussines solo pueden tener cuentas CC")) : Mono.just(Boolean.TRUE));
    }

    public Mono<Boolean> verifyMype(String idClient,Flux<AccountDao> listAccount,Flux<CreditDao> listCredit) {
        return listCredit.any(credit -> true)
                .flatMap(ac -> ac ? verifyMypeAccount(listAccount) : Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente MYPE tiene que tener al menos un credito")));
    }

    public Mono<Boolean> verifyMypeAccount(Flux<AccountDao> listAccount){
        return listAccount
                .filter(account -> account.getAccountCategory().equals(AccountCategory.normal) && account.getType().equals(AccountType.CC))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente MYPE tiene que tener al menos una cuenta CC NORMAL")))
                .hasElements();
    }
}
