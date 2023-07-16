package com.atorres.nttdata.productomicroservice.model;

import com.atorres.nttdata.productomicroservice.utils.AccountCategory;
import com.atorres.nttdata.productomicroservice.utils.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestAccount {
    @JsonProperty("type")
    private AccountType type;
    private BigDecimal balance;
    @JsonProperty("category")
    private AccountCategory accountCategory;
}
