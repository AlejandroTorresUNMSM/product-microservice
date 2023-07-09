package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("product")
public class ProductDao {
    @Id
    private String Id;
    private String name;
    private String type;
    private List<ClientDao> listClient;
    private List<CreditDao> listCredit;
    private List<AccountDao> listAccount;

}
