package com.atorres.nttdata.productomicroservice.service.strategy;

import com.atorres.nttdata.productomicroservice.utils.AccountType;
import com.atorres.nttdata.productomicroservice.utils.ClientType;
import com.atorres.nttdata.productomicroservice.utils.ProductType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class StrategyFactory {
    private final Map<ClientType, AccountStrategy> strategies = new EnumMap<>(ClientType.class);

    public StrategyFactory() {
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
