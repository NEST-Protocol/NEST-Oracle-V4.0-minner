package com.nest.ib.service;

import com.nest.ib.model.Wallet;


public interface MiningService {

    void offer(Wallet wallet);


    void closePriceSheets(Wallet wallet);
}
