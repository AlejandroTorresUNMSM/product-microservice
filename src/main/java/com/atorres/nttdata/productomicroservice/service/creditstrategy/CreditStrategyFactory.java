package com.atorres.nttdata.productomicroservice.service.creditstrategy;

import com.atorres.nttdata.productomicroservice.utils.ClientType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class CreditStrategyFactory {
    private final Map<ClientType, CreditStrategy> strategies = new EnumMap<>(ClientType.class);

    public CreditStrategyFactory() {
        initStrategies();
    }

    public CreditStrategy getStrategy(ClientType userType) {
        if (userType == null || !strategies.containsKey(userType)) {
            throw new IllegalArgumentException("Invalid " + userType);
        }
        return strategies.get(userType);
    }

    private void initStrategies() {
        strategies.put(ClientType.personal, new PersonalCreditStrategy());
        strategies.put(ClientType.bussines, new BussinesCreditStrategy());
    }
}
