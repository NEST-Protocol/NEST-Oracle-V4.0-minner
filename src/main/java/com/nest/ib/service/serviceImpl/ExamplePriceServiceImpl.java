package com.nest.ib.service.serviceImpl;

import com.nest.ib.service.PriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author wll
 * @date 2021/12/6 14:34
 */
//@Service
public class ExamplePriceServiceImpl implements PriceService {

    @Override
    public BigDecimal getToken0Token1Price() {
        return BigDecimal.ONE;
    }
}
