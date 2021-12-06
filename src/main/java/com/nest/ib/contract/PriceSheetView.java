package com.nest.ib.contract;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.generated.Uint152;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;

import java.math.BigInteger;

public class PriceSheetView extends StaticStruct {

    public PriceSheetView(
            BigInteger index,
            String miner,
            BigInteger height,
            BigInteger remainNum,
            BigInteger ethNumBal,
            BigInteger tokenNumBal,
            BigInteger nestNum1k,
            BigInteger level,
            BigInteger shares,
            BigInteger price) {
        super(new Uint32(index),
                new Address(miner),
                new Uint32(height),
                new Uint32(remainNum),
                new Uint32(ethNumBal),
                new Uint32(tokenNumBal),
                new Uint24(nestNum1k),
                new Uint8(level),
                new Uint8(shares),
                new Uint152(price));

        this.index = index;
        this.miner = new Address(miner);
        this.height = height;
        this.remainNum = remainNum;
        this.ethNumBal = ethNumBal;
        this.tokenNumBal = tokenNumBal;
        this.nestNum1k = nestNum1k;
        this.level = level;
        this.shares = shares;
        this.price = price;
    }

    public PriceSheetView(Uint32 index,
                          Address miner,
                          Uint32 height,
                          Uint32 remainNum,
                          Uint32 ethNumBal,
                          Uint32 tokenNumBal,
                          Uint24 nestNum1k,
                          Uint8 level,
                          Uint8 shares,
                          Uint152 price) {
        super(index,
                miner,
                height,
                remainNum,
                ethNumBal,
                tokenNumBal,
                nestNum1k,
                level,
                shares,
                price);

        this.index = index.getValue();
        this.miner = miner;
        this.height = height.getValue();
        this.remainNum = remainNum.getValue();
        this.ethNumBal = ethNumBal.getValue();
        this.tokenNumBal = tokenNumBal.getValue();
        this.nestNum1k = nestNum1k.getValue();
        this.level = level.getValue();
        this.shares = shares.getValue();
        this.price = price.getValue();
    }

    // Index corresponding to quotation sheet
    public BigInteger index;
    // Quotator address
    public Address miner;
    // The height of the block where the quotation is located
    public BigInteger height;
    // How much ETH/ Token can be eaten? As long as it is eaten, its value will decrease. When it reaches 0, the quotation can no longer be eaten
    public BigInteger remainNum;
    // It is used to record the change of the quotation ETH quantity, and settle the remaining ETH funds when the quotation is closed
    public BigInteger ethNumBal;
    // It is used to record the change of the number of tokens on the quotation sheet and settle the remaining token funds when the quotation sheet is closed
    public BigInteger tokenNumBal;
    /**
     * Quote 1 ETH for each time, corresponding to the number of Nest to be frozen.
     * NestNum1K is a freezing Nest factor, which can be multiplied by 1000 to get the number of Nest frozen for this quotation/order.
     * This value will change when the order is eaten.
     */
    public BigInteger nestNum1k;
    // Record the eating state, and when the value is 1-4, freeze twice the ETH, and when the value is 5-127, freeze twice the NEST
    public BigInteger level;
    // Charge for quotation sheet
    public BigInteger shares;
    // 1 Number of ETH exchangeable tokens (price)
    public BigInteger price;


    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public Address getMiner() {
        return miner;
    }

    public void setMiner(Address miner) {
        this.miner = miner;
    }

    public BigInteger getHeight() {
        return height;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    public BigInteger getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(BigInteger remainNum) {
        this.remainNum = remainNum;
    }

    public BigInteger getEthNumBal() {
        return ethNumBal;
    }

    public void setEthNumBal(BigInteger ethNumBal) {
        this.ethNumBal = ethNumBal;
    }

    public BigInteger getTokenNumBal() {
        return tokenNumBal;
    }

    public void setTokenNumBal(BigInteger tokenNumBal) {
        this.tokenNumBal = tokenNumBal;
    }

    public BigInteger getNestNum1k() {
        return nestNum1k;
    }

    public void setNestNum1k(BigInteger nestNum1k) {
        this.nestNum1k = nestNum1k;
    }

    public BigInteger getLevel() {
        return level;
    }

    public void setLevel(BigInteger level) {
        this.level = level;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public BigInteger getShares() {
        return shares;
    }

    public void setShares(BigInteger shares) {
        this.shares = shares;
    }
}
