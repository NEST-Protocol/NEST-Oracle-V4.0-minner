package com.nest.ib.vo;

import com.nest.ib.model.Wallet;
import com.nest.ib.service.MiningService;

import com.nest.ib.helper.WalletHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;


@Component
public class OfferTask {
    @Autowired
    private MiningService miningService;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }

    /**
     * Quote: the ETH/ERC20
     */
    @Scheduled(fixedDelay = 3000, initialDelay = 1 * 60 * 1000)
    public void offer() {
        Wallet wallet = WalletHelper.getWallet();
        if (wallet == null) return;
        miningService.offer(wallet);
    }

    /**
     * Close quotation, unfreeze assets, batch unfreeze, only quotation account can unfreeze their own quotation
     */
    @Scheduled(fixedDelay = 120 * 1000, initialDelay = 1 * 60 * 1000)
    public void close() {
        Wallet wallet = WalletHelper.getWallet();
        if (wallet == null) return;
        miningService.closePriceSheets(wallet);
    }

}
