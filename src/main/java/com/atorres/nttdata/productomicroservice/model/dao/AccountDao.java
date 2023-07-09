package com.atorres.nttdata.productomicroservice.model.dao;

import com.atorres.nttdata.productomicroservice.utils.AccountType;
import lombok.Data;

@Data
public class AccountDao {
    private AccountType type;
    private Double balance;
}
