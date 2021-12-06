package com.nest.ib.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author wll
 * @date 2020/12/28 16:15
 */
public class Web3jHelper {

    private static final Logger log = LoggerFactory.getLogger(Web3jHelper.class);

    private static Web3j web3j;

    private static String NODE_URL;


    public static String getNode() {
        return NODE_URL;
    }

    public static Web3j getWeb3j() {
        return web3j;
    }

    public static boolean updateWeb3j(String nodeUrl) {

        if (StringUtils.isEmpty(nodeUrl)) return false;

        Web3j wj = null;
        try {
            wj = Web3j.build(new HttpService(nodeUrl));
            wj.ethGasPrice().send();
        } catch (Exception e) {
            log.error("The added node cannot connect to Ethereum:{}", e.getMessage());
            return false;
        }

        if (wj == null) return false;
        NODE_URL = nodeUrl;
        web3j = wj;

        return true;
    }
}
