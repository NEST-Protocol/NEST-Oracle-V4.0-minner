package com.nest.ib.constant;


import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author wll
 * @date 2020/7/16 13:22
 */
public interface Constant {

    BigDecimal UNIT_ETH = new BigDecimal("1000000000000000000");

    BigDecimal UNIT_DEC18 = new BigDecimal("1000000000000000000");

    BigInteger UNIT_INT18 = new BigInteger("1000000000000000000");

    BigInteger BIG_INTEGER_1K = BigInteger.valueOf(1000);

    BigInteger BIG_INTEGER_200K = BigInteger.valueOf(200000);

    BigInteger BIG_INTEGER_FIVE = BigInteger.valueOf(5);
}
