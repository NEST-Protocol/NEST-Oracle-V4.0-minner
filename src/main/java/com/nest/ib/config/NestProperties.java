package com.nest.ib.config;

import com.nest.ib.constant.Constant;
import com.nest.ib.contract.NestMiningContract;
import com.nest.ib.contract.Config;
import com.nest.ib.state.Erc20State;
import com.nest.ib.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author wll
 * @date 2020/12/29 19:08
 */
@Configuration
@ConfigurationProperties(prefix = "nest")
public class NestProperties {

    private static final Logger log = LoggerFactory.getLogger(NestProperties.class);

    @Autowired
    private Erc20State erc20State;

    // bsc chainId
    private Long chainId;

    private String nestTokenAddress;

    private String nestMiningAddress;

    public boolean updateContractParams(Web3j web3j) {

        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, nestMiningAddress);
        NestMiningContract miningContract = NestMiningContract.load(nestMiningAddress, web3j, readonlyTransactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        Config config = null;
        try {
            config = miningContract.getConfig().send();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Gets the contract parameter exception :{}", e.getMessage());
            return false;
        }
        if (config == null) return false;
        this.setNestStakedNum(config.nestStakedNum1k.multiply(Constant.BIG_INTEGER_1K));
        this.setPriceDurationBlock(config.priceDurationBlock);
        this.setMaxBiteNestedLevel(config.maxBiteNestedLevel.intValue());

        return true;
    }

    /**
     * Comparison of quotation procedures
     */
    private BigDecimal miningFee;

    /**
     * Quotation procedure ratio calculation factor
     */
    private BigDecimal miningFeeRateInflateFactor;

    /**
     * Eat a single charge than the column
     */
    private BigDecimal biteFeeRate;

    /**
     * The number of NEST collateral required for each quotation
     */
    private BigInteger nestStakedNum;

    /**
     * Validation period
     */
    private BigInteger priceDurationBlock;

    /**
     * Take a single quoted assets the maximum times of doubling, doubling times more than this value, the quoted assets will not continue to double
     */
    private Integer maxBiteNestedLevel;

    /**
     * Eat single quoted assets to increase multiple
     */
    private BigInteger biteInflateFactor;

    /**
     * When you eat a single meal, your NEST increases multiple times, doubling every time you eat a single meal, no matter how long you eat a single meal
     */
    private BigInteger biteNestInflateFactor;


    public String getNestMiningAddress() {
        return nestMiningAddress;
    }

    public void setNestMiningAddress(String nestMiningAddress) {
        this.nestMiningAddress = nestMiningAddress;
    }

    public String getNestTokenAddress() {
        return nestTokenAddress;
    }

    public void setNestTokenAddress(String nestTokenAddress) {
        this.nestTokenAddress = nestTokenAddress;
    }

    public BigDecimal getMiningFeeRateInflateFactor() {
        return miningFeeRateInflateFactor;
    }

    public void setMiningFeeRateInflateFactor(BigDecimal miningFeeRateInflateFactor) {
        this.miningFeeRateInflateFactor = miningFeeRateInflateFactor;
    }

    public BigDecimal getBiteFeeRate() {
        return biteFeeRate;
    }

    public void setBiteFeeRate(BigDecimal biteFeeRate) {
        this.biteFeeRate = biteFeeRate;
        log.info("BiteFeeRate update：{}", biteFeeRate);
    }

    public BigInteger getNestStakedNum() {
        return nestStakedNum;
    }

    public void setNestStakedNum(BigInteger nestStakedNum) {
        this.nestStakedNum = nestStakedNum;
        log.info("NestStakedNum update：{}", nestStakedNum);
    }

    public BigInteger getPriceDurationBlock() {
        return priceDurationBlock;
    }

    public void setPriceDurationBlock(BigInteger priceDurationBlock) {
        this.priceDurationBlock = priceDurationBlock;
        log.info("PriceDurationBlock update：{}", priceDurationBlock);
    }

    public Integer getMaxBiteNestedLevel() {
        return maxBiteNestedLevel;
    }

    public void setMaxBiteNestedLevel(Integer maxBiteNestedLevel) {
        this.maxBiteNestedLevel = maxBiteNestedLevel;
        log.info("MaxBiteNestedLevel update：{}", maxBiteNestedLevel);
    }

    public BigInteger getBiteInflateFactor() {
        return biteInflateFactor;
    }

    public void setBiteInflateFactor(BigInteger biteInflateFactor) {
        this.biteInflateFactor = biteInflateFactor;
        log.info("BiteInflateFactor update：{}", biteInflateFactor);
    }

    public BigInteger getBiteNestInflateFactor() {
        return biteNestInflateFactor;
    }

    public void setBiteNestInflateFactor(BigInteger biteNestInflateFactor) {
        this.biteNestInflateFactor = biteNestInflateFactor;
        log.info("BiteNestInflateFactor update：{}", biteNestInflateFactor);
    }

    public Erc20State getErc20State() {
        return erc20State;
    }

    public void setErc20State(Erc20State erc20State) {
        this.erc20State = erc20State;
    }

    public BigDecimal getMiningFee() {
        return miningFee;
    }

    public void setMiningFee(BigDecimal miningFee) {
        this.miningFee = miningFee;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }
}
