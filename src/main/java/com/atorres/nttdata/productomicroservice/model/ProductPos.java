package com.atorres.nttdata.productomicroservice.model;

import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import com.atorres.nttdata.productomicroservice.model.dao.ClientDao;
import com.atorres.nttdata.productomicroservice.model.dao.CreditDao;
import lombok.Data;

import java.util.List;

@Data
public class ProductPos {
    private List<String> listClient;
}
