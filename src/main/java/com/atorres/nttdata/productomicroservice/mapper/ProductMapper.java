package com.atorres.nttdata.productomicroservice.mapper;

import com.atorres.nttdata.productomicroservice.model.ProductPos;
import com.atorres.nttdata.productomicroservice.model.RequestProductPersonal;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Component
public class ProductMapper {
    public ProductDao  productToproductDao(RequestProductPersonal productPos){
        return ProductDao.builder()
                .id(generateId())
                .type("personal")
                .listClientId(Collections.singletonList(productPos.getClientId()))
                .listAccount(productPos.getAccountList())
                .listAuthorized(Collections.singletonList(productPos.getClientId()))
                .build();
    }


    private String generateId(){
        return java.util.UUID.randomUUID().toString().replaceAll("-","");
    }
}
