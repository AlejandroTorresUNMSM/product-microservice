package com.atorres.nttdata.productomicroservice.model;

import com.atorres.nttdata.productomicroservice.utils.AccountType;
import lombok.Data;

@Data
public class RequestAccount {
    private AccountType type;
    private Double balance;
}
