package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("client-product")
@Builder
@ToString
public class ClientProductDao {
    @Id
    private String id;
    private String client;
    private String product;
    private String category;
}
