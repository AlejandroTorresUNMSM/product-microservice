package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.mapper.ProductMapper;
import com.atorres.nttdata.productomicroservice.model.ProductPos;
import com.atorres.nttdata.productomicroservice.model.RequestProductPersonal;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import com.atorres.nttdata.productomicroservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactivefeign.spring.config.ReactiveFeignNamedContextFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private WebClientMicroservice webClientMicroservice;


    public Flux<ClientDao> findAllClient(){
        return webClientMicroservice.getClients();
    }

    public Mono<ClientDao> findClientId(String id){
        return webClientMicroservice.getClientById(id);
    }

    public Flux<ProductDao> findAll(){
        return productRepository.findAll();
    }

    public Mono<ProductDao> createProductPersonal(RequestProductPersonal productPersonal){
        Flux<ClientDao> listClientDao = webClientMicroservice.getClients();
        //revisar que existan los clientes
        Mono<Boolean> existPersonal = listClientDao.any(clientDao -> clientDao.getId().equals(productPersonal.getClientId()) && clientDao.getTypeClient().equals("personal"));
        //verificar que se cumpla las reglas de las cuentas

       return existPersonal.flatMap(exist -> exist ?  Mono.just(new ProductDao()) :  Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El client no existe o no es tipo personal")) );
    }

    public Mono<ProductDao> createProductBussines(ProductPos productPos){
        Flux<ClientDao> listClientDao = webClientMicroservice.getClients();
        //revisar que existan los clientes
        verifyClientBussines(productPos.getListClient(),listClientDao);
        //revisar si son dos o mas clientes que pertenezcan al tipo bussines
        //Mono<String> typep = this.verifyTypeProduct(Flux.fromIterable(productPos.getListClient()));

        //ejecutar las reglas de producto segun el tipo



        return Mono.just(new ProductDao());
  }


    private List<AccountDao> createAccount(ProductPos productPos){
        return new ArrayList<>();
    }

    private List<CreditDao> createCredit(ProductPos productPos){
        return new ArrayList<>();
    }

    /**
    private void verifyClient(List<String> listId,Flux<ClientDao>  listClientDao){
        Flux.fromIterable(listId)
                .filter(id -> listClientDao.any(cliente -> Objects.equals(cliente.getId(), id)))
                .collectList()
                .doOnNext(idsFaltantes -> {
                    if (!idsFaltantes.isEmpty()) {
                        throw new RuntimeException("Los siguientes IDs no se encuentran en la lista de clientes totales: " + idsFaltantes);
                    }
                })
                .block();
    }**/
    private Flux<AccountDao> verifyAccountPersonal(Flux<AccountDao> listAccount){
        return listAccount;
    }

    private void verifyClientBussines(List<String> listId,Flux<ClientDao>  listClientDao){

    }

    private Mono<String> verifyTypeProduct(Flux<ClientDao> listClient){
        return listClient
                .map(ClientDao::getTypeClient)
                .distinct()
                .reduce((prev, current) -> prev.equals(current) ? prev : "")
                .filter(categoria -> !categoria.isEmpty())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"Los clientes no pertenecen al mismo tipo")));
    }


}
