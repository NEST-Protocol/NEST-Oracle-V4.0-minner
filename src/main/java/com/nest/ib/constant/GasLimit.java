package com.nest.ib.constant;

import java.math.BigInteger;

/**
 * @author wll
 * @date 2020/12/30 14:24
 */
public interface GasLimit {

    BigInteger CLOSE_GAS_LIMIT = new BigInteger("600000");

    BigInteger OFFER_GAS_LIMIT = new BigInteger("700000");

    BigInteger CANCEL_GAS_LIMIT = new BigInteger("200000");

    BigInteger APPROVE_GAS_LIMIT = new BigInteger("100000");

    BigInteger DEFAULT_GAS_LIMIT = new BigInteger("600000");
}
