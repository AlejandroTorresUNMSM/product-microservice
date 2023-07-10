package com.atorres.nttdata.productomicroservice.model;

import lombok.Data;

@Data
public class RequestUpdateAccount {
    private Double balance;
    private String accountId;
    private String clientId;
}
