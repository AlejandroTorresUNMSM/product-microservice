package com.atorres.nttdata.productomicroservice.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestCredit {
    private BigDecimal balance;
}
