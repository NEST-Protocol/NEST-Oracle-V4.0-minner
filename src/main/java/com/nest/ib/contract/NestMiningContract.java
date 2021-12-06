package com.nest.ib.contract;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class NestMiningContract extends Contract {

    protected NestMiningContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NestMiningContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }


    public RemoteFunctionCall<Config> getConfig() {
        final Function function = new Function("getConfig",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Config>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Config.class);
    }


    public RemoteFunctionCall<PriceChannelView> getChannelInfo(BigInteger channelId) {
        final Function function = new Function("getChannelInfo",
                Arrays.<Type>asList(new Uint256(channelId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<PriceChannelView>() {
                }));
        return executeRemoteCallSingleValueReturn(function, PriceChannelView.class);
    }

    public RemoteFunctionCall<List> list(BigInteger channelId, BigInteger offset, BigInteger count, BigInteger order) {
        final Function function = new Function("list",
                Arrays.<Type>asList(new Uint(channelId), new Uint(offset), new Uint(count), new Uint(order)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<PriceSheetView>>() {
                }));
        return executeRemoteCallSingleValueReturn(function, List.class);
    }

    public RemoteCall<BigInteger> balanceOf(String tokenAddress, String miner) {
        final Function function = new Function("balanceOf",
                Arrays.<Type>asList(new Address(tokenAddress), new Address(miner)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> estimate(BigInteger channelId) {
        final Function function = new Function("estimate",
                Arrays.<Type>asList(new Uint256(channelId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }


    public static NestMiningContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NestMiningContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static NestMiningContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NestMiningContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
