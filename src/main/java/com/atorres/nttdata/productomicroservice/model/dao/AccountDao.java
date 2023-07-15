package com.atorres.nttdata.productomicroservice.model.dao;

import com.atorres.nttdata.productomicroservice.utils.AccountCategory;
import com.atorres.nttdata.productomicroservice.utils.AccountType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("account")
@Builder
@ToString
public class AccountDao {
    @Id
    private String id;
    private AccountType type;
    private Double balance;
    private AccountCategory accountCategory;
}
