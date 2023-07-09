package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document("product")
public class ProductDao {
    @Id
    private String id;
    private String type;
    private List<String> listClientId;
    private List<CreditDao> listCredit;
    private List<AccountDao> listAccount;
    private List<String> listAuthorized;

}
