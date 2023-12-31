package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.model.RequestClientproduct;
import com.atorres.nttdata.productomicroservice.model.RequestCredit;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.repository.CreditRepository;
import com.atorres.nttdata.productomicroservice.service.creditstrategy.CreditStrategy;
import com.atorres.nttdata.productomicroservice.service.creditstrategy.CreditStrategyFactory;
import com.atorres.nttdata.productomicroservice.utils.RequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditService {
  /**
   *Cliente que conecta client_microservice
   */
  @Autowired
  private WebClientMicroservice webClientMicroservice;
	/**
	 * Repositorio que guarda la relacion cliente-producto
	 */
  @Autowired
  private ClientProductRepository clientProductRepository;
	/**
	 * Mapper para creditosDao
	 */
  @Autowired
  private RequestMapper requestMapper;
	/**
	 * Repositorio para creditos
	 */
  @Autowired
  private CreditRepository creditRepository;
	/**
	 * Clase que divide las reglas para los creditos segun el tipo cliente
	 */
  @Autowired
  private CreditStrategyFactory strategy;

	/**
	 * Metodo que crea un credito
	 * @param clientId id cliente
	 * @param requestCredit request
	 * @return clientproduct
	 */
  public Mono<ClientProductDao> createCredit(String clientId, RequestCredit requestCredit) {
    //obtenemos el cliente
    return webClientMicroservice.getClientById(clientId)
            .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe")))
            .flatMap(clientdao -> {
              //obtenemos todas las cuentas agregando la nueva
              Flux<CreditDao> creditAll = this.getAllCreditByClient(clientId).concatWith(Flux.just(requestMapper.requestToDao(requestCredit)));
              //seleccionamos la estrategia para el tipo de cliente
              CreditStrategy strategyCredit = strategy.getStrategy(clientdao.getTypeClient());
              return strategyCredit.verifyCredit(creditAll).flatMap(exist -> Boolean.FALSE.equals(exist) ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El credito no cumplen los requisitos"))
                      : creditRepository.save(requestMapper.requestToDao(requestCredit)).flatMap(accountDao -> clientProductRepository.save(requestMapper.cpToDaoCredit(clientdao, accountDao))));
            });
  }

	/**
	 * Metodo que trae todos los credito de un cliente
	 * @param clientId id cliente
	 * @return credito
	 */
  public Flux<CreditDao> getAllCreditByClient(String clientId) {
    return clientProductRepository.findByClient(clientId)
            .filter(cp -> cp.getCategory().equals("credit"))
            .flatMap(cp -> creditRepository.findAll().filter(accountDao -> accountDao.getId().equalsIgnoreCase(cp.getProduct())));
  }

	/**
	 * Metodo que elimina un credito
	 * @param requestClientproduct request
	 * @return vacio
	 */
  public Flux<Void> delete(RequestClientproduct requestClientproduct) {
    return clientProductRepository.findAll()
            .filter(cp -> cp.getCategory().equals("credit"))
            .filter(cp -> cp.getClient().equals(requestClientproduct.getClient()))
            .filter(cp -> cp.getProduct().equals(requestClientproduct.getProduct()))
            .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No se encontro la relacion client-producto")))
            .flatMap(cp -> creditRepository.findById(cp.getProduct())
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Existe la relacion pero no se encontró el credito"))))
                    .flatMap(account -> clientProductRepository.deleteById(cp.getId())
                            .then(creditRepository.deleteById(cp.getProduct())))

            );
  }
}
