package com.atorres.nttdata.productomicroservice.utils;

import com.atorres.nttdata.productomicroservice.model.RequestAccount;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientProductDao;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RequestMapper {
    public AccountDao accountToDao(RequestAccount requestAccount){
        return AccountDao.builder()
                .id(generateId())
                .type(requestAccount.getType())
                .balance(requestAccount.getBalance())
                .build();
    }

    public ClientProductDao cpToDao(ClientDao client, AccountDao product){
        return ClientProductDao.builder()
                .id(generateId())
                .category("account")
                .clientid(client.getId())
                .productid(product.getId())
                .build();
    }

    private String generateId(){
        return java.util.UUID.randomUUID().toString().replaceAll("-","");
    }
}
