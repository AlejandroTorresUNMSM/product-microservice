package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestClientproduct;
import com.atorres.nttdata.productomicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.repository.AccountRepository;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.repository.CreditRepository;
import com.atorres.nttdata.productomicroservice.service.accountstrategy.AccountStrategy;
import com.atorres.nttdata.productomicroservice.service.accountstrategy.AccountStrategyFactory;
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
    private AccountStrategyFactory accountStrategyFactory;
    @Autowired
    private CreditService creditService;

    /**
     * Funcion que crear una cuenta segun el id del cliente y el requestaccount
     * @param clientId id del cliente
     * @param requestAccount request con los datos de la cuenta
     * @return response la relacion client-product
     */
    public Mono<ClientProductDao> createAccount(String clientId, RequestAccount requestAccount) {
        //obtenemos el cliente
        return webClientMicroservice.getClientById(clientId)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND,"El cliente no existe")))
                .flatMap(clientdao -> {
                    //obtenemos todas las cuentas agregando la nueva
                    Flux<AccountDao> accountAll = this.getAllAccountsByClient(clientId).concatWith(Flux.just(requestMapper.accountToDao(requestAccount)));
                    //seleccionamos la estrategia para el tipo de cliente
                    AccountStrategy strategy = accountStrategyFactory.getStrategy(clientdao.getTypeClient());
                    return strategy.verifyClient(accountAll,Mono.just(requestAccount.getAccountCategory()),clientId,this.getAllCredit(clientId))
                            .flatMap(exist -> !exist ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "La cuenta no cumplen los requisitos"))
                                    : accountRepository.save(requestMapper.accountToDao(requestAccount)).flatMap(accountDao -> {
                              //guardamos la relacion client-product
                              return clientProductRepository.save(requestMapper.cpToDaoAccount(clientdao, accountDao));
                            }));
                });
    }

    /**
     * Metodo para obtener todas las cuentas de un cliente
     * @param clientId id de cliente
     * @return devuelve una lista de cuentas
     */
    public Flux<AccountDao> getAllAccountsByClient(String clientId) {
        return clientProductRepository.findByClient(clientId)
                .filter(cp -> cp.getCategory().equals("account"))
                .flatMap(cp -> accountRepository.findAll().filter(accountDao -> accountDao.getId().equalsIgnoreCase(cp.getProduct())));
    }

    /**
     * Funcion que elimina una cuenta segun el clientproduct que pasemos
     * @param requestClientproduct request clientproduct
     * @return retorna un void
     */
    public Flux<Void> delete(RequestClientproduct requestClientproduct) {
        return clientProductRepository.findAll()
                .filter(cp -> cp.getCategory().equals("account"))
                .filter(cp -> cp.getClient().equals(requestClientproduct.getClient()))
                .filter(cp -> cp.getProduct().equals(requestClientproduct.getProduct()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND,"No se encontro la relacion client-producto")))
                .flatMap(cp -> accountRepository.findById(cp.getProduct())
                        .switchIfEmpty(Mono.defer(() ->Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Existe la relacion pero no se encontró el producto"))))
                        .flatMap(account -> clientProductRepository.deleteById(cp.getId())
                                .then(accountRepository.deleteById(cp.getProduct()))
                                .doOnSuccess(v -> log.info("Cuenta eliminada con exito")))
                );
    }

    public Mono<AccountDao> update(RequestUpdateAccount request){
        return clientProductRepository.findAll()
                .filter(cp -> cp.getCategory().equals("account"))
                .filter(cp -> cp.getClient().equals(request.getClientId()))
                .filter(cp -> cp.getProduct().equals(request.getAccountId()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No la relacion client-producto")))
                .single()
                .flatMap(cp -> accountRepository.findById(cp.getProduct())
                        .flatMap(account ->{
                            //Actualizando balance
                            account.setBalance(request.getBalance());
                            return accountRepository.save(account);
                        })
                        .switchIfEmpty(Mono.defer(() ->Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Existe la cuenta"))))
                );
    }

  private Flux<CreditDao> getAllCredit(String clientId) {
    return creditService.getAllCreditByClient(clientId);
  }

}
