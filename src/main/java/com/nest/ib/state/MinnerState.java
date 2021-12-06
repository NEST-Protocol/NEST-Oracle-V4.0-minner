package com.nest.ib.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * @author wll
 * @date 2021/01/28 15:48
 * Mining machine related status value
 */
@Component
public class MinnerState {

    private static final Logger log = LoggerFactory.getLogger(MinnerState.class);

    /**
     * Mining machine state, off by default
     */
    private volatile boolean open;

    /**
     * Quote block interval: Default is 10
     */
    private volatile BigInteger blockInterval = BigInteger.TEN;

    /**
     * Minimum quantity of each batch defrost quotation
     */
    private volatile int closeMinNum = 1;

    /**
     * Number of queries each time the contract quotation list is called: Default 50
     */
    private volatile BigInteger maxFindNum = new BigInteger("50");

    public boolean isOpen() {
        return open;
    }

    public void closeMiner() {
        this.open = false;
        log.info("The miner has been shut down");
    }

    public void openMiner() {
        this.open = true;
        log.info("The miner is on");
    }

    public int getCloseMinNum() {
        return closeMinNum;
    }

    public void setCloseMinNum(int closeMinNum) {
        this.closeMinNum = closeMinNum;
    }

    public BigInteger getMaxFindNum() {
        return maxFindNum;
    }

    public void setMaxFindNum(BigInteger maxFindNum) {
        this.maxFindNum = maxFindNum;
    }

    public BigInteger getBlockInterval() {
        return blockInterval;
    }

    public void setBlockInterval(BigInteger blockInterval) {
        this.blockInterval = blockInterval;
    }

}
