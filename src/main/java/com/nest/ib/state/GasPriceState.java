package com.nest.ib.state;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author wll
 * @date 2020/12/30 11:10
 */
@Component
public class GasPriceState {

    /**
     * The basic quotation GasPrice multiple, the default 1.1 times
     */
    public Type baseOfferType = new Type(new BigDecimal("1"));

    /**
     * Defrost the asset GASPrice
     */
    public Type closeSheet = new Type(new BigDecimal("1"));

    /**
     * Take out the assets :1.1 times
     */
    public Type withdrawType = new Type(new BigDecimal("1.1"));

    /**
     * Authorized transactions: 1.2 times
     */
    public Type approveType = new Type(new BigDecimal("1.1"));

    /**
     * Cancel the transaction GasPrice: Quick-cancel, default 1.2x, based on the quoted GasPrice 1.2x cancel
     */
    private volatile BigDecimal cancelOfferGasPriceMul = new BigDecimal("1.2");


    public static class Type {
        private volatile BigDecimal gasPriceMul = BigDecimal.ONE;

        public Type(BigDecimal gasPriceMul) {
            this.gasPriceMul = gasPriceMul;
        }

        public BigDecimal getGasPriceMul() {
            return gasPriceMul;
        }

        public void setGasPriceMul(BigDecimal gasPriceMul) {
            this.gasPriceMul = gasPriceMul;
        }
    }

    public Type getBaseOfferType() {
        return baseOfferType;
    }

    public void setBaseOfferType(Type baseOfferType) {
        this.baseOfferType = baseOfferType;
    }

    public Type getCloseSheet() {
        return closeSheet;
    }

    public void setCloseSheet(Type closeSheet) {
        this.closeSheet = closeSheet;
    }

    public BigDecimal getCancelOfferGasPriceMul() {
        return cancelOfferGasPriceMul;
    }

    public void setCancelOfferGasPriceMul(BigDecimal cancelOfferGasPriceMul) {
        this.cancelOfferGasPriceMul = cancelOfferGasPriceMul;
    }

    public Type getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(Type withdrawType) {
        this.withdrawType = withdrawType;
    }

    public Type getApproveType() {
        return approveType;
    }

    public void setApproveType(Type approveType) {
        this.approveType = approveType;
    }
}
