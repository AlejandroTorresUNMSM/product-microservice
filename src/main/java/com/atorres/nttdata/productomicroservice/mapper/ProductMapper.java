package com.atorres.nttdata.productomicroservice.mapper;

import com.atorres.nttdata.productomicroservice.model.ProductPos;
import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDao  postToDao (ProductPos productPos){
        ProductDao productDao = new ProductDao ();
        productDao.setId(generateId());
        productDao.setName(productPos.getName());
        productDao.setType(productPos.getListClient().get(0).getTypeClient());
        productDao.setListClient(productPos.getListClient());
        productDao.setListAccount(productPos.getListAccount());
        productDao.setListCredit(productPos.getListCredit());
        return productDao;
    }

    public ProductDao postToDao (ProductPos productPos, String id){
        ProductDao productDao = new ProductDao ();
        productDao.setId(id);
        productDao.setName(productPos.getName());
        productDao.setType(productPos.getListClient().get(0).getTypeClient());
        productDao.setListClient(productPos.getListClient());
        productDao.setListAccount(productPos.getListAccount());
        productDao.setListCredit(productPos.getListCredit());
        return productDao;
    }

    private String generateId(){
        return java.util.UUID.randomUUID().toString().replaceAll("-","");
    }
}
