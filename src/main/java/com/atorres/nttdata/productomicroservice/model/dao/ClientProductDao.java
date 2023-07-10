package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("cliente-product")
@Builder
public class ClientProductDao {
    @Id
    private String id;
    private String clientid;
    private String productid;
    private String category;
}
