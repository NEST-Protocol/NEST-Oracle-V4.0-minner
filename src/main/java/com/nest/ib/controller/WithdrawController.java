package com.nest.ib.controller;

import com.nest.ib.config.NestProperties;
import com.nest.ib.constant.Constant;
import com.nest.ib.model.R;
import com.nest.ib.helper.WalletHelper;
import com.nest.ib.model.Wallet;
import com.nest.ib.state.Erc20State;
import com.nest.ib.utils.EthClient;
import com.nest.ib.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;


@RestController
@RequestMapping("/withdraw")
public class WithdrawController {

    @Autowired
    private Erc20State erc20State;
    @Autowired
    private NestProperties nestProperties;
    @Autowired
    private EthClient ethClient;
    @Autowired
    private WalletHelper walletHelper;

    @GetMapping("")
    public ModelAndView miningData() {
        ModelAndView mav = new ModelAndView("withdraw");
        mav.addObject("src", "/withdraw");

        walletHelper.updateWalletBalance();

        // gasPrice
        mav.addObject("close", WalletHelper.getWallet().getClosed());
        mav.addObject("erc20State", erc20State);
        mav.addObject("nestProperties", nestProperties);

        return mav;
    }

    @PostMapping("/withdrawToken")
    public R withdrawToken(@RequestParam(name = "tokenAamount") BigDecimal tokenAamount,
                           @RequestParam(name = "tokenAddress") String tokenAddress,
                           @RequestParam(name = "decimal") BigDecimal decimal) {
        BigInteger token = tokenAamount.multiply(decimal).toBigInteger();
        boolean b = ethClient.withdraw(tokenAddress, token, WalletHelper.getWallet());
        if (b) return R.ok();

        return R.error();
    }

    @PostMapping("/withdrawNest")
    public R withdrawNest(@RequestParam(name = "nestAamount") BigDecimal nestAamount) {
        if (WalletHelper.getWallet() == null) {
            return R.error("Wallet is empty, please add address private key first");
        }
        BigInteger nest = MathUtils.decMulInt(nestAamount, Constant.UNIT_INT18).toBigInteger();
        boolean b = ethClient.withdraw(nestProperties.getNestTokenAddress(), nest, WalletHelper.getWallet());
        if (b) {
            return R.ok("Transaction sent successfully");
        }

        return R.error();
    }

}
