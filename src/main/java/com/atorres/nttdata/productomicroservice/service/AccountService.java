package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.repository.AccountRepository;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.utils.RequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WebClientMicroservice webClientMicroservice;
    @Autowired
    private ClientProductRepository clientProductRepository;
    @Autowired
    private RequestMapper requestMapper;

    public Mono<ClientProductDao> createAccount(String clientId, RequestAccount requestAccount){
        //obtenemos el cliente
        Mono<ClientDao> client = webClientMicroservice.getClientById(clientId);

        //obtenemos las cuentas del cliente y añado la nueva cuenta
        //Flux<AccountDao> accountAll = getAllAccountsByClient(clientId).concatWith(Flux.just(requestMapper.accountToDao(requestAccount)));

        //revisamos que la cuenta cumpla el tipo del cliente
        /**client.flatMap(clientdao -> {
            System.out.println("");
            return switch (clientdao.getTypeClient()) {
                case "personal" -> this.verifyAccountPersonal(accountAll);
                case "bussines" -> this.verifyAccountPersonal(accountAll);
                default -> Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No exista el tipo del cliente"));
            };
        }).subscribe();  **/
        //Guardamos el producto
        //Mono<AccountDao> accountDaoMono = accountRepository.save(requestMapper.accountToDao(requestAccount));
        //Guardamos la relacion clientProduct
         //return clientProductRepository.save(requestMapper.cpToDao(Objects.requireNonNull(client.block()), Objects.requireNonNull(accountDaoMono.block())));

        return client
                .flatMap(clientdao -> {
                    //obtenemos todas las cuentas agregando la nueva
                    Flux<AccountDao> accountAll = getAllAccountsByClient(clientId).concatWith(Flux.just(requestMapper.accountToDao(requestAccount)));

                    //guardamos el producto
                    return accountRepository.save(requestMapper.accountToDao(requestAccount)).flatMap(accountDao -> {
                        //con el productoid creamos el clientproduct
                        return clientProductRepository.save(requestMapper.cpToDao(clientdao, accountDao));
                    });
                });
    }

    public Flux<AccountDao> getAllAccountsByClient(String clientId){
        return clientProductRepository.findByClientid(clientId)
                .filter(cp -> cp.getCategory().equals("account"))
                .flatMap(cp -> accountRepository.findById(cp.getProductid()));
    }

    private Mono<Boolean> verifyAccountPersonal(Flux<AccountDao> listAccount) {
        return listAccount
                .groupBy(AccountDao::getType)
                .flatMap(group -> group.count().map(count -> Pair.of(group.key(), count)))
                .collectList()
                .map(groups -> groups.size() <= 3 && groups.size() >= 1 && groups.stream().allMatch(pair -> pair.getSecond() == 1))
                .filter(result -> result)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se cumplen las condiciones requeridas para los tipos de cuenta personal")));
    }
}
