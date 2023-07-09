package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CreditDao {
    @Id
    private String id;
    private String clientId;
    private String type;
    private Double balance;
    private Double debt;
}
