package com.nest.ib.contract;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint96;

import java.math.BigInteger;

/**
 * 合约参数
 */
public class PriceChannelView extends StaticStruct {

    public PriceChannelView(BigInteger channelId,
                            BigInteger sheetCount,
                            BigInteger feeInfo,
                            String token0,
                            BigInteger unit,
                            String token1,
                            BigInteger rewardPerBlock,
                            String reward,
                            BigInteger vault,
                            String governance,
                            BigInteger genesisBlock,
                            BigInteger postFeeUnit,
                            BigInteger singleFee,
                            BigInteger reductionRate) {
        super(new Uint256(channelId),
                new Uint256(sheetCount),
                new Uint256(feeInfo),
                new Address(token0),
                new Uint96(unit),
                new Address(token1),
                new Uint96(rewardPerBlock),
                new Address(reward),
                new Uint96(vault),
                new Address(governance),
                new Uint32(genesisBlock),
                new Uint16(postFeeUnit),
                new Uint16(singleFee),
                new Uint16(reductionRate));

        this.channelId = channelId;
        this.sheetCount = sheetCount;
        this.feeInfo = feeInfo;
        this.token0 = token0;
        this.unit = unit;
        this.token1 = token1;
        this.rewardPerBlock = rewardPerBlock;
        this.reward = reward;
        this.vault = vault;
        this.governance = governance;
        this.genesisBlock = genesisBlock;
        this.postFeeUnit = postFeeUnit;
        this.singleFee = singleFee;
        this.reductionRate = reductionRate;

    }

    public PriceChannelView(Uint256 channelId,
                            Uint256 sheetCount,
                            Uint256 feeInfo,
                            Address token0,
                            Uint96 unit,
                            Address token1,
                            Uint96 rewardPerBlock,
                            Address reward,
                            Uint96 vault,
                            Address governance,
                            Uint32 genesisBlock,
                            Uint16 postFeeUnit,
                            Uint16 singleFee,
                            Uint16 reductionRate) {
        super(channelId,
                sheetCount,
                feeInfo,
                token0,
                unit,
                token1,
                rewardPerBlock,
                reward,
                vault,
                governance,
                genesisBlock,
                postFeeUnit,
                singleFee,
                reductionRate);

        this.channelId = channelId.getValue();
        this.sheetCount = sheetCount.getValue();
        this.feeInfo = feeInfo.getValue();
        this.token0 = token0.getValue();
        this.unit = unit.getValue();
        this.token1 = token1.getValue();
        this.rewardPerBlock = rewardPerBlock.getValue();
        this.reward = reward.getValue();
        this.vault = vault.getValue();
        this.governance = governance.getValue();
        this.genesisBlock = genesisBlock.getValue();
        this.postFeeUnit = postFeeUnit.getValue();
        this.singleFee = singleFee.getValue();
        this.reductionRate = reductionRate.getValue();
    }


    public BigInteger channelId;
    public BigInteger sheetCount;
    public BigInteger feeInfo;
    // 计价代币地址, 0表示eth
    public String token0;
    // 计价token0代币单位
    public BigInteger unit;
    // 报价代币地址，0表示eth
    public String token1;
    public BigInteger rewardPerBlock;

    // 矿币地址如果和token0或者token1是一种币，可能导致挖矿资产被当成矿币挖走
    // 出矿代币地址
    public String reward;
    // 矿币总量
    public BigInteger vault;
    // 管理地址
    public String governance;
    public BigInteger genesisBlock;
    // Post fee(0.0001eth，DIMI_ETHER). 1000
    public BigInteger postFeeUnit;
    // Single query fee (0.0001 ether, DIMI_ETHER). 100
    public BigInteger singleFee;
    // 衰减系数，万分制。8000
    public BigInteger reductionRate;

}
