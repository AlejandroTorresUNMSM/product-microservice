package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("credit")
public class CreditDao {
    @Id
    private String id;
    private Double balance;
    private Double debt;
}
