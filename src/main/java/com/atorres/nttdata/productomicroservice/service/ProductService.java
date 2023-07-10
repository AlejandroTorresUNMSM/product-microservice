package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.mapper.ProductMapper;
import com.atorres.nttdata.productomicroservice.model.RequestProductPersonal;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import com.atorres.nttdata.productomicroservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private WebClientMicroservice webClientMicroservice;

    public Flux<ProductDao> findAll() {
        return productRepository.findAll();
    }

    public Mono<ProductDao> createProductPersonal(RequestProductPersonal productPersonal) {
        Flux<ClientDao> listClientDao = webClientMicroservice.getClients();
        Flux<ProductDao> listProduct = productRepository.findAll();
        //revisar que existan los clientes
        Mono<Boolean> existPersonal = listClientDao.any(clientDao -> clientDao.getId().equals(productPersonal.getClientId()) && clientDao.getTypeClient().equals("personal"));
        //verificar que el client no tenga otros productos
        Mono<Boolean> productExist = this.verifyExistProduct(Collections.singletonList(productPersonal.getClientId()),listProduct);

        //verificar que se cumpla las reglas de las cuentas personales
        Mono<Boolean> verifyAccount = this.verifyAccountPersonal(Flux.fromIterable(productPersonal.getAccountList()));

        return verifyAccount.flatMap(exist -> exist ? productRepository.save(productMapper.productToproductDao(productPersonal)) : Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "error error")));
    }

    /**Por arreglar **/
    private Mono<Boolean> verifyExistProduct(List<String> listClientId,Flux<ProductDao> listProduct){
        return Flux.fromIterable(listClientId)
                .flatMap(clientId -> listProduct
                        .filter(product -> product.getListClientId().contains(clientId))
                        .hasElements()
                        .flatMap(exists -> exists
                                ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El cliente ya tiene productos"))
                                : Mono.just(false)))
                .any(Boolean::booleanValue);
    }


    /**
     * Funcion para clientes personal que revisa que el cliente tenga maximo una cuenta de cada tipo
     *
     * @param listAccount Listas de cuenta del request
     * @return Retorna un true si cumple con los requisitos
     */

    private Mono<Boolean> verifyAccountPersonal(Flux<AccountDao> listAccount) {
        return listAccount
                .groupBy(AccountDao::getType)
                .flatMap(group -> group.count().map(count -> Pair.of(group.key(), count)))
                .collectList()
                .map(groups -> groups.size() <= 3 && groups.size() >= 1 && groups.stream().allMatch(pair -> pair.getSecond() == 1))
                .filter(result -> result)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se cumplen las condiciones requeridas para los tipos de cuenta personal")));
    }

    private void verifyClientBussines(List<String> listId, Flux<ClientDao> listClientDao) {

    }

    private Mono<String> verifyTypeProduct(Flux<ClientDao> listClient) {
        return listClient
                .map(ClientDao::getTypeClient)
                .distinct()
                .reduce((prev, current) -> prev.equals(current) ? prev : "")
                .filter(categoria -> !categoria.isEmpty())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Los clientes no pertenecen al mismo tipo")));
    }


}
