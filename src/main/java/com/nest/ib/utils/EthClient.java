package com.nest.ib.utils;

import com.nest.ib.config.NestProperties;
import com.nest.ib.constant.Constant;
import com.nest.ib.constant.GasLimit;
import com.nest.ib.contract.*;
import com.nest.ib.model.Wallet;
import com.nest.ib.helper.Web3jHelper;
import com.nest.ib.state.Erc20State;
import com.nest.ib.state.GasPriceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author wll
 * @date 2020/7/16 13:25
 */
@Component
public class EthClient {
    private static final Logger log = LoggerFactory.getLogger(EthClient.class);

    @Autowired
    private Erc20State erc20State;
    @Autowired
    private GasPriceState gasPriceState;
    @Autowired
    private NestProperties nestProperties;

    /**
     * Check Transaction Status
     *
     * @param hash
     * @return
     */
    public EthGetTransactionReceipt ethGetTransactionReceipt(String hash) {
        EthGetTransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = Web3jHelper.getWeb3j().ethGetTransactionReceipt(hash).send();
        } catch (IOException e) {
            log.error("ethGetTransactionReceipt 异常：{}", e.getMessage());
        }
        return transactionReceipt;
    }

    /**
     * Check to see if one-time authorization has taken place, and if not, one-time authorization has taken place
     */
    public void approveToNestMinningContract(Wallet wallet) throws Exception {
        log.info("Authorization checking");
        BigInteger nonce = ethGetTransactionCount(wallet.getCredentials().getAddress());
        if (nonce == null) {
            log.error("Failed to obtain nonce：{}", nonce);
            throw new Exception("Failed to obtain nonce");
        }
        // Authorization of the token
        String token0ApproveHash = erc20Appprove(wallet, erc20State.token, nonce);
        nonce = StringUtils.isEmpty(token0ApproveHash) ? nonce : nonce.add(BigInteger.ONE);
        // Ntoken authorization
        String token1ApproveHash = erc20Appprove(wallet, erc20State.token1, nonce);

        nonce = StringUtils.isEmpty(token1ApproveHash) ? nonce : nonce.add(BigInteger.ONE);
        Erc20State.Item nest = new Erc20State.Item();
        nest.setAddress(nestProperties.getNestTokenAddress());
        nest.setSymbol("NEST");
        String nestApproveHash = erc20Appprove(wallet, nest, nonce);
    }

    public String erc20Appprove(Wallet wallet, Erc20State.Item token, BigInteger nonce) throws ExecutionException, InterruptedException {
        if (token.getAddress().equalsIgnoreCase(Address.DEFAULT.getValue())) return null;

        String transactionHash = null;
        BigInteger gasPrice = ethGasPrice(gasPriceState.approveType);
        if (gasPrice == null) throw new NullPointerException("Authorization check Failed to get GasPrice");

        BigInteger approveValue = allowance(wallet, token.getAddress());
        if (approveValue == null) {
            log.error("Authorization to obtain ApproveValue failed：{}", approveValue);
        }

        if (approveValue.compareTo(new BigInteger("100000000000000")) <= 0) {

            List<Type> typeList = Arrays.<Type>asList(
                    new Address(nestProperties.getNestMiningAddress()),
                    new Uint256(new BigInteger("999999999999999999999999999999999999999999"))
            );
            Function function = new Function("approve", typeList, Collections.<TypeReference<?>>emptyList());
            String encode = FunctionEncoder.encode(function);
            BigInteger payableEth = BigInteger.ZERO;

            RawTransaction tokenRawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    GasLimit.APPROVE_GAS_LIMIT,
                    token.getAddress(),
                    payableEth,
                    encode);

            EthSendTransaction ethSendTransaction = ethSendRawTransaction(wallet.getCredentials(), tokenRawTransaction);
            if (ethSendTransaction.hasError()) {
                Response.Error error = ethSendTransaction.getError();
                log.error("Authorization transaction return failed：[msg = {}],[data = {}],[code = {}],[result = {}],[RawResponse = {}],",
                        error.getMessage(),
                        error.getData(),
                        error.getCode(),
                        ethSendTransaction.getResult(),
                        ethSendTransaction.getRawResponse());
            } else {
                transactionHash = ethSendTransaction.getTransactionHash();
                log.info("{} : {} One-time authorization hash： {} ", wallet.getCredentials().getAddress(), token.getSymbol(), transactionHash);
            }
        }
        return transactionHash;
    }

    public boolean checkTxStatus(String txHash, BigInteger nonce, String minnerAddress) {

        if (StringUtils.isEmpty(txHash)) {
            log.error("Transaction hash is empty, stop detecting transaction status!");
            return false;
        }

        if (!checkNonce(nonce, minnerAddress)) {
            log.error(String.format("Current transaction exception, hash：%s", txHash));
            return false;
        }

        log.info(String.format("Check the transaction status hash：%s", txHash));
        Optional<TransactionReceipt> transactionReceipt = ethGetTransactionReceipt(txHash).getTransactionReceipt();
        if (transactionReceipt == null) return false;
        if (!transactionReceipt.isPresent()) {
            log.error(String.format("The transaction has been overwritten, %s", txHash));
            return false;
        }

        int status = Integer.parseInt(transactionReceipt.get().getStatus().substring(2));
        return status == 1;
    }

    private boolean checkNonce(BigInteger nonce, String address) {
        BigInteger transactionCount = ethGetTransactionCount(address);
        if (nonce.compareTo(transactionCount) < 0) {
            log.info("Trading nonce has changed");
            return true;
        }

        log.info("The transaction is in progress. Repeat the test after 3 seconds !");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return checkNonce(nonce, address);
    }

    /**
     * Query token totalSupply
     *
     * @param erc20Address
     * @return
     */
    public BigInteger totalSupply(String erc20Address) {
        BigInteger totalSupply = null;
        try {
            totalSupply = ContractBuilder.erc20Readonly(erc20Address, Web3jHelper.getWeb3j()).totalSupply().send();
        } catch (Exception e) {
            log.error("Total number of substitutions on the chain failed:{}", e.getMessage());
        }

        return totalSupply;
    }

    /**
     * Get the latest quote block number
     *
     * @return
     */
    public BigInteger checkLatestMining() {
        try {
            NestMiningContract nestMiningContract = ContractBuilder.nestMiningContract(Web3jHelper.getWeb3j());
            // Get the last 5 pieces of data through List interface in reverse order, and then traverse Level ==0 to get the latest quotation
            List<PriceSheetView> list = nestMiningContract.list(erc20State.channelId, BigInteger.ZERO, Constant.BIG_INTEGER_FIVE, BigInteger.ZERO).send();
            if (list.size() == 0) {
                log.info("This is the first quotation");
                return BigInteger.ZERO;
            }

            for (PriceSheetView priceSheetView : list) {
                if (priceSheetView.level.compareTo(BigInteger.ZERO) == 0) {
                    return priceSheetView.height;
                }
            }
            // The quotation of mining with the first quotation has not been found for 5 times in a row, indicating that it is five times in a row. Although the possibility is very small, we still need to deal with it here
            // Skip the last 5 strokes and retrieve the new 5 strokes
            list = nestMiningContract.list(erc20State.channelId, Constant.BIG_INTEGER_FIVE, Constant.BIG_INTEGER_FIVE, BigInteger.ZERO).send();
            for (PriceSheetView priceSheetView : list) {
                if (priceSheetView.level.compareTo(BigInteger.ZERO) == 0) {
                    return priceSheetView.height;
                }
            }

        } catch (Exception e) {
            log.error("checkLatestMining failure:{}", e.getMessage());
        }
        return null;
    }

    public BigInteger ethGasPrice(GasPriceState.Type type) {
        BigInteger gasPrice = null;

        try {
            gasPrice = Web3jHelper.getWeb3j().ethGasPrice().send().getGasPrice();
        } catch (IOException e) {
            log.error("Failed to get GasPrice：{}", e.getMessage());
        }
        if (gasPrice == null) return gasPrice;
        gasPrice = MathUtils.toDecimal(gasPrice).multiply(type.getGasPriceMul()).toBigInteger();

        return gasPrice;
    }

    /**
     * View the authorized amount
     *
     * @param wallet
     * @return
     */
    public BigInteger allowance(Wallet wallet, String erc20TokenAddress) {
        BigInteger approveValue = null;
        try {
            approveValue = ContractBuilder.erc20Readonly(erc20TokenAddress, Web3jHelper.getWeb3j()).allowance(wallet.getCredentials().getAddress(), nestProperties.getNestMiningAddress()).sendAsync().get();
        } catch (InterruptedException e) {
            log.error("The query for authorization amount failed：{}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("The query for authorization amount failed：{}", e.getMessage());
        }
        return approveValue;
    }

    /**
     * For the nonce value
     *
     * @param address
     * @return
     */
    public BigInteger ethGetTransactionCount(String address) {
        BigInteger transactionCount = null;

        try {
            transactionCount = Web3jHelper.getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        } catch (Exception e) {
            log.error("Failed to get nonce:{}", e.getMessage());
        }
        return transactionCount;
    }

    /**
     * Get the latest block number for Ethereum
     *
     * @return
     */
    public BigInteger ethBlockNumber() {
        BigInteger latestBlockNumber = null;

        try {
            latestBlockNumber = Web3jHelper.getWeb3j().ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            log.error("Failed to get the latest block number：{}", e.getMessage());
        }

        return latestBlockNumber;
    }

    public BigInteger ethGetBalance(String address) {
        BigInteger balance = null;
        try {
            balance = Web3jHelper.getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        } catch (IOException e) {
            log.error("Failed to query ETH balance：{}", e.getMessage());
        }
        return balance;
    }

    public BigInteger itemBalanceOfInContract(Erc20State.Item item, String miner) {
        if (item.getZero()) return BigInteger.ZERO;
        return balanceOfInContract(item.getAddress(), miner);
    }


    /**
     * Query the available balance of tokens within the specified miner's contract
     *
     * @param tokenAddress
     * @param miner
     * @return
     */
    public BigInteger balanceOfInContract(String tokenAddress, String miner) {
        BigInteger r = null;
        try {
            r = ContractBuilder.nestMiningContract(Web3jHelper.getWeb3j()).balanceOf(tokenAddress, miner).send();
        } catch (Exception e) {
            log.error("Failed to get ERC20 unfrozen balance in the contract：{}", e.getMessage());
        }
        return r;
    }

    /**
     * Gets a frozen quotation at a specified address
     *
     * @param miner      Address of Quotation Miner
     * @param maxFindNum The maximum number of quotations traversed, which is also the number of arrays returned, is 0 if it does not meet the conditions
     * @return
     */
    public List<PriceSheetView> unClosedSheetListOf(String miner, BigInteger maxFindNum) {
        List<PriceSheetView> list = list(erc20State.channelId, BigInteger.ZERO, maxFindNum, BigInteger.ZERO);

        List<PriceSheetView> reulst = null;
        if (!CollectionUtils.isEmpty(list)) {
            reulst = new ArrayList<>();
            for (PriceSheetView priceSheetView : list) {
                if (!priceSheetView.miner.getValue().equalsIgnoreCase(miner)
                        || (priceSheetView.ethNumBal.compareTo(BigInteger.ZERO) <= 0
                        && priceSheetView.tokenNumBal.compareTo(BigInteger.ZERO) <= 0))
                    continue;

                reulst.add(priceSheetView);
            }
        }

        return reulst;
    }

    /**
     * List quotations in pages
     *
     * @param channelId
     * @param offset    Skip the previous offset bar record
     * @param count     Returns the count entry
     * @param order     Sort way. 0 reverse order, non-0 positive order
     * @return
     */
    public List<PriceSheetView> list(BigInteger channelId, BigInteger offset, BigInteger count, BigInteger order) {
        List<PriceSheetView> sheetList = null;
        try {
            sheetList = ContractBuilder.nestMiningContract(Web3jHelper.getWeb3j()).list(channelId, offset, count, order).send();
        } catch (Exception e) {
            log.error("List interface query failed: {}", e);
        }
        return sheetList;
    }

    /**
     * Gets the INDEX of the last quotation in the specified quote list
     *
     * @param channelId
     * @return
     */
    public BigInteger lastIndex(BigInteger channelId) {
        List<PriceSheetView> list = list(channelId, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO);
        if (CollectionUtils.isEmpty(list)) {
            return BigInteger.ZERO;
        }
        return list.get(0).index;
    }

    /**
     * Gets a collection of unthawed quotations at the specified address
     *
     * @return
     */
    public List<Uint256> canClosedSheetIndexs(List<PriceSheetView> list) {
        if (CollectionUtils.isEmpty(list)) return null;

        BigInteger blockNumber = ethBlockNumber();
        if (blockNumber == null) {
            log.error("canClosedSheetIndexs : Failed to get the latest block number");
            return null;
        }

        int size = list.size();

        List<Uint256> closeIndexs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PriceSheetView priceSheetView = list.get(i);
            BigInteger height = priceSheetView.getHeight();
            if (blockNumber.subtract(height).compareTo(nestProperties.getPriceDurationBlock()) > 0) {
                closeIndexs.add(new Uint256(priceSheetView.getIndex()));
            }
        }

        return closeIndexs;
    }


    public BigInteger balanceOfItem(Erc20State.Item item, String user) {
        if (item.getZero()) return ethGetBalance(user);
        return ethBalanceOfErc20(user, item.getAddress());
    }

    /**
     * Gets the balance of the ERC20 token for the specified account
     *
     * @param address           The account address
     * @param erc20TokenAddress
     * @return
     */
    public BigInteger ethBalanceOfErc20(String address, String erc20TokenAddress) {
        BigInteger balance = null;
        try {
            balance = ContractBuilder.erc20Readonly(erc20TokenAddress, Web3jHelper.getWeb3j()).balanceOf(address).send();
        } catch (Exception e) {
            log.error("Query for ERC20 balance failed：{}", e.getMessage());
        }
        return balance;
    }


    public boolean withdraw(String tokenAddress, BigInteger tokenAmount, Wallet wallet) {

        BigInteger gasPrice = ethGasPrice(gasPriceState.withdrawType);
        BigInteger nonce = ethGetTransactionCount(wallet.getCredentials().getAddress());
        if (gasPrice == null || nonce == null) {
            log.info("gasPrice || nonce fail to get");
            return false;
        }

        List<Type> typeList = Arrays.<Type>asList(
                new Address(tokenAddress),
                new Uint256(tokenAmount));

        Function function = new Function("withdraw", typeList, Collections.<TypeReference<?>>emptyList());
        String encode = FunctionEncoder.encode(function);
        String transaction = null;
        try {
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    GasLimit.DEFAULT_GAS_LIMIT,
                    nestProperties.getNestMiningAddress(),
                    BigInteger.ZERO,
                    encode);
            EthSendTransaction ethSendTransaction = ethSendRawTransaction(wallet.getCredentials(), rawTransaction);
            transaction = ethSendTransaction.getTransactionHash();
            if (ethSendTransaction.hasError()) {
                Response.Error error = ethSendTransaction.getError();
                log.error("Withdraw transaction return failed: [msg = {}],[data = {}],[code = {}],[result = {}],[RawResponse = {}],",
                        error.getMessage(),
                        error.getData(),
                        error.getCode(),
                        ethSendTransaction.getResult(),
                        ethSendTransaction.getRawResponse());
            }
            log.info("Withdraw transaction hash:{}", transaction);
        } catch (Exception e) {
            log.error("Sending a Withdraw transaction fails：{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Send quote transaction POST/POST2
     *
     * @param wallet
     * @param gasPrice
     * @param nonce
     * @param typeList
     * @param payableEth
     * @return
     */
    public String offer(String method, Wallet wallet, BigInteger gasPrice, BigInteger nonce, List typeList, BigInteger payableEth) {
        Function function = new Function(method, typeList, Collections.<TypeReference<?>>emptyList());
        String encode = FunctionEncoder.encode(function);
        String transaction = null;
        try {
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    GasLimit.OFFER_GAS_LIMIT,
                    nestProperties.getNestMiningAddress(),
                    payableEth,
                    encode);
            EthSendTransaction ethSendTransaction = ethSendRawTransaction(wallet.getCredentials(), rawTransaction);
            transaction = ethSendTransaction.getTransactionHash();
            if (ethSendTransaction.hasError()) {
                Response.Error error = ethSendTransaction.getError();
                log.error("Quoted transaction return failed：[msg = {}],[data = {}],[code = {}],[result = {}],[RawResponse = {}],",
                        error.getMessage(),
                        error.getData(),
                        error.getCode(),
                        ethSendTransaction.getResult(),
                        ethSendTransaction.getRawResponse());
            }
        } catch (Exception e) {
            log.error("Send {} quote transaction failed：{}", method, e.getMessage());
        }
        return transaction;
    }

    /**
     * Bulk defrost quotation
     *
     * @param wallet
     * @param indices
     */
    public String close(Wallet wallet, BigInteger nonce, List<Uint256> indices) {
        if (CollectionUtils.isEmpty(indices)) return null;
        Credentials credentials = wallet.getCredentials();
        if (nonce == null) {
            log.error("{} ：close failed to get nonce", credentials.getAddress());
            return null;
        }

        BigInteger gasPrice = ethGasPrice(gasPriceState.closeSheet);
        if (gasPrice == null) {
            log.error("close : failed to get GasPrice");
            return null;
        }

        List<Type> typeList = Arrays.<Type>asList(
                new Uint256(erc20State.channelId),
                new DynamicArray(Uint256.class, indices));
        Function function = new Function("close", typeList, Collections.<TypeReference<?>>emptyList());
        String encode = FunctionEncoder.encode(function);

        BigInteger payableEth = BigInteger.ZERO;

        String transactionHash = null;
        try {

            // Batch defrosting due to the size is larger than the fixed, here estimated gas
            org.web3j.protocol.core.methods.request.Transaction transaction =
                    org.web3j.protocol.core.methods.request.Transaction.
                            createFunctionCallTransaction(
                                    wallet.getCredentials().getAddress(),
                                    nonce,
                                    gasPrice,
                                    null,
                                    nestProperties.getNestMiningAddress(),
                                    encode);
            BigInteger amountUsed = getTransactionGasLimit(transaction);
            if (amountUsed == null) {
                amountUsed = GasLimit.CLOSE_GAS_LIMIT;
            } else {
                amountUsed = amountUsed.add(Constant.BIG_INTEGER_200K);
            }

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    amountUsed,
                    nestProperties.getNestMiningAddress(),
                    payableEth,
                    encode);

            EthSendTransaction ethSendTransaction = ethSendRawTransaction(credentials, rawTransaction);
            transactionHash = ethSendTransaction.getTransactionHash();
            if (ethSendTransaction.hasError()) {
                Response.Error error = ethSendTransaction.getError();
                log.error("Closelist transaction return failed：[msg = {}],[data = {}],[code = {}],[result = {}],[RawResponse = {}],",
                        error.getMessage(),
                        error.getData(),
                        error.getCode(),
                        ethSendTransaction.getResult(),
                        ethSendTransaction.getRawResponse());
            }
        } catch (Exception e) {
            log.error("Failed to send Closelist transaction：{}", e.getMessage());
        }
        return transactionHash;
    }

    public BigInteger getTransactionGasLimit(org.web3j.protocol.core.methods.request.Transaction transaction) {
        BigInteger amountUsed = null;
        try {
            EthEstimateGas ethEstimateGas = Web3jHelper.getWeb3j().ethEstimateGas(transaction).send();
            if (ethEstimateGas.hasError()) {
                log.error("Estimate GasLimit exceptions：{}", ethEstimateGas.getError().getMessage());
                return amountUsed;
            }
            amountUsed = ethEstimateGas.getAmountUsed();
        } catch (IOException e) {
            log.error("Estimate GasLimit exceptions：{}", e.getMessage());
        }
        return amountUsed;
    }

    /**
     * Cancel the transaction (use the same nonce as the quoted price, set the GasPrice higher than the quoted price and make a transfer to yourself to override the quoted transaction)
     */
    public String cancelTransaction(Wallet wallet, BigInteger nonce, BigInteger gasPrice) {
        BigInteger payableEth = BigInteger.ZERO;
        String transactionHash = null;
        try {
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    GasLimit.CANCEL_GAS_LIMIT,
                    wallet.getCredentials().getAddress(),
                    payableEth);
            EthSendTransaction ethSendTransaction = ethSendRawTransaction(wallet.getCredentials(), rawTransaction);

            transactionHash = ethSendTransaction.getTransactionHash();
            if (ethSendTransaction.hasError()) {
                Response.Error error = ethSendTransaction.getError();
                log.error("Cancel transaction returns failure：[msg = {}],[data = {}],[code = {}],[result = {}],[RawResponse = {}],",
                        error.getMessage(),
                        error.getData(),
                        error.getCode(),
                        ethSendTransaction.getResult(),
                        ethSendTransaction.getRawResponse());
            }
        } catch (Exception e) {
            log.error("Cancellation transaction failed to send:{}", e.getMessage());
        }
        return transactionHash;
    }

    public EthSendTransaction ethSendRawTransaction(Credentials credentials, RawTransaction rawTransaction) throws ExecutionException, InterruptedException {

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, nestProperties.getChainId(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        Web3j web3j = Web3jHelper.getWeb3j();
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        return ethSendTransaction;
    }

}
