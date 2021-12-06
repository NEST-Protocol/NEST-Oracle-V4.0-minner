package com.nest.ib.helper;

import com.nest.ib.contract.PriceSheetView;
import com.nest.ib.service.MiningService;
import com.nest.ib.utils.EthClient;
import com.nest.ib.config.NestProperties;
import com.nest.ib.constant.Constant;
import com.nest.ib.model.Wallet;
import com.nest.ib.state.Erc20State;
import com.nest.ib.state.MinnerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author wll
 * @date 2020/8/24 9:47
 */
@Component
public class WalletHelper {

    private static final Logger log = LoggerFactory.getLogger(WalletHelper.class);
    private static Wallet WALLET;

    @Autowired
    private EthClient ethClient;
    @Autowired
    private NestProperties nestProperties;
    @Autowired
    private Erc20State erc20State;
    @Autowired
    private MinnerState minnerState;
    @Autowired
    private MiningService miningService;


    public static void updateWallet(Wallet wallet) {
        WALLET = wallet;
    }

    public static Wallet getWallet() {
        if (WALLET == null) {
            log.warn("The wallet is empty");
            return null;
        }

        return WALLET;
    }

    /**
     * Update wallet assets
     */
    public void updateWalletBalance() {
        if (WALLET != null) {
            if (!erc20State.updateToken0Token1Price()) return;
            updateBalance(WALLET, true);
        }
    }


    /**
     * Check the balance：
     * 1.ETH balance check: check whether the balance of CLOSE in the contract is sufficient.
     * 1.1 If it is sufficient, the balance of ETH+ CLOSE will be determined to be sufficient; if it is sufficient, the balance will be charged to the balance + commission
     * 1.2 can be a one-time into a number of quotation required ETH, such as into 600ETH, each quotation only into the handling fee can be
     * 2. Check token balance to determine whether the balance of CLOSE + account balance meets the quoted price
     * 3. If it is post2, check the NTOKEN balance
     * 4.NEST balance check to determine whether CLSOE's balance + account balance meets the quotation, and 100,000 NEST is required for each quotation pair. If it is Post2, two quotation pairs will be generated for each quotation
     *
     * @param sumTotal true Count all assets (account + unfrozen + frozen), false does not count all assets
     */
    public boolean updateBalance(Wallet wallet, boolean sumTotal) {

        // Default asset shortage
        wallet.setRich(false);
        boolean rich = true;
        String address = wallet.getCredentials().getAddress();
        // Get the ETH balance of the account
        BigInteger ethBalance = ethClient.ethGetBalance(address);
        if (ethBalance == null) {
            return false;
        }

        // Get the account token0 balance
        BigInteger token0Balance = ethClient.balanceOfItem(erc20State.token, address);
        if (token0Balance == null) {
            return false;
        }

        // Get the account token1 balance
        BigInteger token1Balance = ethClient.balanceOfItem(erc20State.token1, address);
        if (token1Balance == null) {
            return false;
        }

        // Get the NEST balance of your account
        BigInteger nestBalance = ethClient.ethBalanceOfErc20(address, nestProperties.getNestTokenAddress());
        if (nestBalance == null) {
            return false;
        }

        log.info("{} account balance：ETH={}，{}={}，{}={}，nest={}", address, ethBalance, erc20State.token.getSymbol(), token0Balance, erc20State.token1.getSymbol(), token1Balance, nestBalance);
        Wallet.Asset account = wallet.getAccount();
        account.setEthAmount(ethBalance);
        account.setNestAmount(nestBalance);
        account.setTokenAmount(token0Balance);
        account.setToken1Amount(token1Balance);


        // Get the token balances of CLOSE
        BigInteger closeToken0 = ethClient.itemBalanceOfInContract(erc20State.token, address);
        BigInteger closeToken1 = ethClient.itemBalanceOfInContract(erc20State.token1, address);

        if (closeToken0 == null || closeToken1 == null) return false;

        Wallet.Asset closed = wallet.getClosed();
        closed.setEthAmount(BigInteger.ZERO);
        closed.setTokenAmount(closeToken0);
        closed.setToken1Amount(closeToken1);

        // Get the NEST balance of close, which contains the number of NEST mined, and can also be used for direct quotation
        BigInteger closeNest = ethClient.balanceOfInContract(nestProperties.getNestTokenAddress(), address);
        if (closeNest == null) return false;
        closed.setNestAmount(closeNest);

        log.info("{} Assets are unfrozen under the contract ，{}={}，{}={}，nest={}", address, erc20State.token.getSymbol(), closeToken0, erc20State.token1.getSymbol(), closeToken1, closeNest);


        // Current Total Available Balance, Account Balance + Unfrozen Balance
        Wallet.Asset useable = wallet.getUseable();
        Wallet.Asset useableTemp = new Wallet.Asset();
        useableTemp.addAsset(closed);
        useableTemp.addAsset(account);
        useable.setAsset(useableTemp);

        // Quotation required assets
        Wallet.Asset offerNeed = wallet.getOfferNeed();

        Assert.isTrue(erc20State.token0Token1Pirce != null, "The token0Token1Pirce is null");
        BigInteger needToken = erc20State.getNeedToken0();
        BigInteger needNest = nestProperties.getNestStakedNum().multiply(Constant.UNIT_INT18);
        BigInteger needToken1 = erc20State.getNeedToken1();

        BigInteger needEth = nestProperties.getMiningFee().multiply(Constant.UNIT_DEC18).toBigInteger();
        if (erc20State.token.getZero()) needEth = needEth.add(needToken);
        if (erc20State.token1.getZero()) needEth = needEth.add(needToken1);

        offerNeed.setEthAmount(needEth);
        offerNeed.setNestAmount(needNest);
        offerNeed.setTokenAmount(needToken);
        offerNeed.setToken1Amount(needToken1);

        BigInteger usableEth = useable.getEthAmount();
        if (usableEth.compareTo(needEth) < 0) {
            log.warn("Insufficient ETH balance, need: {}, available balance: {}", needEth, usableEth);
            rich = false;
        }

        BigInteger usableNest = useable.getNestAmount();

        if (usableNest.compareTo(needNest) < 0) {
            log.warn("Nest balance insufficient, need: {}, available balance: {}", needNest, usableNest);
            rich = false;
        }

        BigInteger usableToken = useable.getTokenAmount();
        if (usableToken.compareTo(needToken) < 0) {
            log.warn("{} insufficient balance, need: {}, available balance: {}", erc20State.token.getSymbol(), needToken, usableToken);
            rich = false;
        }

        // Calculate total assets
        if (sumTotal) {
            Wallet.Asset freezedAssetTemp = new Wallet.Asset();

            // Token quotes freeze assets
            List<PriceSheetView> tokenPriceSheets = ethClient.unClosedSheetListOf(address, minnerState.getMaxFindNum());
            addFreezedAsset(freezedAssetTemp, tokenPriceSheets, erc20State.token);

            wallet.getFreezed().setAsset(freezedAssetTemp);

            Wallet.Asset total = wallet.getTotal();
            Wallet.Asset totalTemp = new Wallet.Asset();
            totalTemp.addAsset(account);
            totalTemp.addAsset(wallet.getFreezed());
            totalTemp.addAsset(closed);
            total.setAsset(totalTemp);
        }

        wallet.setPayableEthAmount(needEth);
        wallet.setRich(rich);
        return rich;
    }

    private void addFreezedAsset(Wallet.Asset freezed, List<PriceSheetView> priceSheets, Erc20State.Item erc20) {
        if (!CollectionUtils.isEmpty(priceSheets)) {
            for (PriceSheetView priceSheet : priceSheets) {
                BigInteger nestNum1k = priceSheet.getNestNum1k();
                BigInteger nestNumBal = nestNum1k.multiply(Constant.BIG_INTEGER_1K);

                // Mortgage of the NEST
                BigInteger nestAmount = nestNumBal.multiply(Constant.UNIT_INT18);
                freezed.addNestAmount(nestAmount);

                // Ethnumbal is the amount of ETH left
                BigInteger freezedToken0 = priceSheet.getEthNumBal().multiply(erc20State.getNeedToken0());
                freezed.addTokenAmount(freezedToken0);

                BigInteger freezedToken1 = priceSheet.getTokenNumBal().multiply(priceSheet.price);
                freezed.addToken1Amount(freezedToken1);
            }
        }
    }

}
