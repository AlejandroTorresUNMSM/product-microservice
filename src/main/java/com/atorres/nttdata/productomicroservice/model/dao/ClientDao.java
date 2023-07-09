package com.atorres.nttdata.productomicroservice.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ClientDao {
    @Id
    private String id;
    private String typeDocument;
    private String nroDocument;
    private String name;
    private String typeClient;
}
