package com.atorres.nttdata.productomicroservice.utils;

import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.RequestCredit;
import com.atorres.nttdata.productomicroservice.model.dao.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RequestMapper {
    public AccountDao accountToDao(RequestAccount requestAccount){
        return AccountDao.builder()
                .id(generateId())
                .type(requestAccount.getType())
                .balance(requestAccount.getBalance())
                .accountCategory(requestAccount.getAccountCategory())
                .build();
    }

    public ClientProductDao cpToDaoAccount(ClientDao client, AccountDao product){
        return ClientProductDao.builder()
                .id(generateId())
                .category("account")
                .subcategory(product.getAccountCategory().toString())
                .client(client.getId())
                .product(product.getId())
                .build();
    }

    public ClientProductDao cpToDaoCredit(ClientDao client, CreditDao creditDao){
        return ClientProductDao.builder()
                .id(generateId())
                .category("credit")
                .subcategory("normal")
                .client(client.getId())
                .product(creditDao.getId())
                .build();
    }

    public CreditDao requestToDao(RequestCredit requestCredit){
        return CreditDao.builder()
                .id(generateId())
                .balance(requestCredit.getBalance())
                .debt(requestCredit.getBalance())
                .build();
    }

    private String generateId(){
        return java.util.UUID.randomUUID().toString().replaceAll("-","");
    }
}
