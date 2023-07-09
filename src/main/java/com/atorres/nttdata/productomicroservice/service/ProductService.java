package com.atorres.nttdata.productomicroservice.service;

import com.atorres.nttdata.productomicroservice.client.ClientFeign;
import com.atorres.nttdata.productomicroservice.exception.CustomException;
import com.atorres.nttdata.productomicroservice.mapper.ProductMapper;
import com.atorres.nttdata.productomicroservice.model.ProductPos;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import com.atorres.nttdata.productomicroservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactivefeign.spring.config.ReactiveFeignNamedContextFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientFeign clientFeign;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ReactiveFeignNamedContextFactory reactiveFeignNamedContextFactory;

    public Flux<ClientDao> findAllClient(){
        return clientFeign.getAll();
    }

    public Flux<ProductDao> findAll(){
        return productRepository.findAll();
    }

    public Mono<ProductDao> create(ProductPos productPos){
        //revisar las cuentas y creditos
        Mono<Boolean> existProduct = productRepository.findByName(productPos.getName()).hasElement();
        return existProduct.flatMap(exist -> exist ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST,"El producto ya existe"))
                : productRepository.save(productMapper.postToDao(productPos)));
    }

    private List<AccountDao> createAccount(ProductPos productPos){
        return new ArrayList<>();
    }

    private List<CreditDao> createCredit(ProductPos productPos){
        return new ArrayList<>();
    }

}
