package com.atorres.nttdata.productomicroservice.service.accountstrategy;

import com.atorres.nttdata.productomicroservice.repository.ClientProductRepository;
import com.atorres.nttdata.productomicroservice.repository.CreditRepository;
import com.atorres.nttdata.productomicroservice.utils.ClientType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class AccountStrategyFactory {
    private final Map<ClientType, AccountStrategy> strategies = new EnumMap<>(ClientType.class);
    private CreditRepository creditRepository;
    private ClientProductRepository clientProductRepository;

    public AccountStrategyFactory() {
        initStrategies();
    }

    public AccountStrategy getStrategy(ClientType userType) {
        if (userType == null || !strategies.containsKey(userType)) {
            throw new IllegalArgumentException("Invalid " + userType);
        }
        return strategies.get(userType);
    }

    private void initStrategies() {
        strategies.put(ClientType.personal, new PersonalAccountStrategy());
        strategies.put(ClientType.bussines, new BussinesAccountStrategy());
    }
}
