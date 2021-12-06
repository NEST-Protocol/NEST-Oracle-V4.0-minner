package com.nest.ib.contract;

import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint8;

import java.math.BigInteger;

public class Config extends StaticStruct {

    public Config(
            BigInteger maxBiteNestedLevel,
            BigInteger priceEffectSpan,
            BigInteger nestPledgeNest) {
        super(
                new Uint8(maxBiteNestedLevel),
                new Uint16(priceEffectSpan),
                new Uint16(nestPledgeNest));

        this.maxBiteNestedLevel = maxBiteNestedLevel;
        this.priceDurationBlock = priceEffectSpan;
        this.nestStakedNum1k = nestPledgeNest;
    }

    public Config(
            Uint8 maxBiteNestedLevel,
            Uint16 priceEffectSpan,
            Uint16 nestPledgeNest) {
        super(
                maxBiteNestedLevel,
                priceEffectSpan,
                nestPledgeNest);

        this.maxBiteNestedLevel = maxBiteNestedLevel.getValue();
        this.priceDurationBlock = priceEffectSpan.getValue();
        this.nestStakedNum1k = nestPledgeNest.getValue();
    }

    public BigInteger nestStakedNum1k;
    public BigInteger priceDurationBlock;
    public BigInteger maxBiteNestedLevel;

}
