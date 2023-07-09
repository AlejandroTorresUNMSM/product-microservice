package com.atorres.nttdata.productomicroservice.model;
import com.atorres.nttdata.productomicroservice.model.dao.AccountDao;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class RequestProductPersonal {
    @NotBlank
    private String clientId;
    private List<AccountDao> accountList;
}
