package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestClientproduct;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.repository.AccountRepository;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.service.accountstrategy.AccountStrategy;
import com.atorres.nttdata.productomicroservice.service.accountstrategy.StrategyFactory;
import com.atorres.nttdata.productomicroservice.utils.RequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WebClientMicroservice webClientMicroservice;
    @Autowired
    private ClientProductRepository clientProductRepository;
    @Autowired
    private RequestMapper requestMapper;
    @Autowired
    private StrategyFactory strategyFactory;

    public Mono<ClientProductDao> createAccount(String clientId, RequestAccount requestAccount) {
        //obtenemos el cliente
        Mono<ClientDao> client = webClientMicroservice.getClientById(clientId);
        return client
                .flatMap(clientdao -> {
                    //obtenemos todas las cuentas agregando la nueva
                    Flux<AccountDao> accountAll = this.getAllAccountsByClient(clientId).concatWith(Flux.just(requestMapper.accountToDao(requestAccount)));
                    //seleccionamos la estrategia para el tipo de cliente
                    AccountStrategy strategy = strategyFactory.getStrategy(clientdao.getTypeClient());
                    return strategy.verifyAccount(accountAll).flatMap(exist -> !exist ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "La cuenta no cumplen los requisitos"))
                            : accountRepository.save(requestMapper.accountToDao(requestAccount)).flatMap(accountDao -> {
                        //guardamos la relacion client-product
                        return clientProductRepository.save(requestMapper.cpToDao(clientdao, accountDao));
                    }));
                });
    }


    /**
     * Metodo para obtener todas las cuentas de un cliente
     *
     * @param clientId id de cliente
     * @return devuelve una lista de cuentas
     */
    public Flux<AccountDao> getAllAccountsByClient(String clientId) {
        return clientProductRepository.findByClient(clientId)
                .filter(cp -> cp.getCategory().equals("account"))
                .flatMap(cp -> accountRepository.findAll().filter(accountDao -> accountDao.getId().equalsIgnoreCase(cp.getProduct())));
    }

    public Flux<Void> delete(RequestClientproduct requestClientproduct) {
        //reviso que el cliente exista
        return clientProductRepository.findAll()
                .filter(cp -> cp.getCategory().equals("account"))
                .filter(cp -> cp.getClient().equals(requestClientproduct.getClient()))
                .filter(cp -> cp.getProduct().equals(requestClientproduct.getProduct()))
                .flatMap(cp -> clientProductRepository.deleteById(cp.getId())
                        .then(accountRepository.findById(requestClientproduct.getProduct()))
                        .flatMap(account -> accountRepository.deleteById(account.getId()))
                        .onErrorResume( er ->Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe el cliente a eliminar"))));
    }

}
