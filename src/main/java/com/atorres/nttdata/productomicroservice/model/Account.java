package com.atorres.nttdata.productomicroservice.model;

import com.atorres.nttdata.productomicroservice.utils.AccountType;
import lombok.Data;

@Data
public class Account {
    private AccountType type;
    private Double balance;
}
