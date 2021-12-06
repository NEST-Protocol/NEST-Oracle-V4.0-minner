package com.nest.ib.contract;

import com.nest.ib.config.NestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;

/**
 * @author wll
 * @date 2020/12/28 23:37
 * Contract object creation
 */
@Component
public class ContractBuilder {

    private static NestProperties nestProperties;

    @Autowired
    public ContractBuilder(NestProperties nestProperties) {
        ContractBuilder.nestProperties = nestProperties;
    }

    public static ERC20 erc20Readonly(String erc20Address,Web3j web3j) {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, erc20Address);
        return ERC20.load(erc20Address, web3j, readonlyTransactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public static NestMiningContract nestMiningContract(Web3j web3j) {
        ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(web3j, nestProperties.getNestMiningAddress());
        return NestMiningContract.load(nestProperties.getNestMiningAddress(), web3j, readonlyTransactionManager, Contract.GAS_PRICE, Contract.GAS_LIMIT);
    }

}
