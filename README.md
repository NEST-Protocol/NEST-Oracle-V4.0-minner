### NEST 4.0 Automatic Quotation Program Operating Instructions

[toc]


#### Introduction

>NEST 4.0 automatic quotation program is a sample program ,which cannot be used directly,It must be redeveloped according to the code logic of this program, Any problems encountered during development can be submitted on Github, and the developers of this program will answer them in time.

>The default values of related parameters of this program, such as block interval, quotation gasPrice multiples, and etc. are not optimal strategies, and users can adjust them according to the matter of fact.

>The main functions of the automatic quotation program are:
   * Check the balance of account and frozen/unfrozen assets.
   * Authorize ERC20 token.
   * Initiate quotation (post quotation).
   * Cancel the quotation (automatically determine whether the latest quotation block number of the contract has changed, if the latest block number is bigger than the block number when you quote, then cancel the quotation to avoid being stuck in the block).
   * Unfreeze quotation assets, support single unfreezing and batch unfreezing.
   * Withdraw the unfreezing assets in the contract.
   * Re-acquire parameters in the contract.

#### Preparation Before Start

1. Implement price interface [PriceService](https://github.com/NEST-Protocol/NEST-Oracle-V4.0-minner/blob/bsc/src/main/java/com/nest/ib/service/PriceService.java).
   * Provide the price of token0token1, that is, how many token1s is a token0 worth

2. Get ready: wallet private key and related assets and nodes.
   * Wallet private key: Generated by mnemonic, can be registered through nestDapp.。
   * assets:
   <br/>Token0, token1 and nest that need to be mortgaged for the quotation of take order, and 100000 nest shall be mortgaged for each take order.
   <br/>Gas consumption of sending transaction (BNB)
   * [BSC chain node](https://docs.binance.org/smart-chain/developer/rpc.html).

#### Start and Close

1. Deploy and run the  program: HTTPS protocol is recommended.
2. Sign in：
   * The default user name is `nest` and the password is `nestqwe123!`.
   * If you need to modify the password, you can modify login.user.name (user name) and login.user.passwd (password) in src/main/resources/application.yml.
3. Stop the Quotation arbitrage program：
   * Stop mining before closing the quotation process, and then wait 10 minutes, and then close the window after all quotation assets are confirmed and unfreezed.


#### Related Settings

1. Node settings (required)：
   * The node address must be set first.
2. Set quote channel ID
3. After the quotation channel ID is determined, click the 'confirm' button, and the token information of token0 and token1 will be printed in the background log. Continue the follow-up operation after checking that the information is correct.
4. Set quotation private key (required):
   * Fill in the private key, the program will perform authorization checks, if not authorized, the program will automatically initiate an authorized transaction, please make sure that the authorized transaction is packaged successfully before you can open the verification arbitrage.
5. GasPrice multiple configuration (doubled on the basis of the default gasPrice, can be adjusted according to the actual situation).
6. Start mining:
   * After the above configuration is completed, mining can be started, and the information such as block height, the number of quotations, and the hash of the quotation transaction can be viewed in the background log.
7. Withdraw assets
   * Only closed quotation assets can be Withdrawed. To retrieve all assets, please close all quotations before Withdrawing.

#### Contract interface @BSC
| Function | Interface | 
| ---- | ---- |
| Quote price | [post](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/main/contracts/interfaces/INestBatchMining.sol#L182) |
| Get quotation list | [list](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/main/contracts/interfaces/INestBatchMining.sol#L200) | 
| Close quotation | [close](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/main/contracts/interfaces/INestBatchMining.sol#L212) | 
| Withdraw assets | [withdraw](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/main/contracts/interfaces/INestBatchMining.sol#L223) | 
| Estimated ore drawing | [estimate](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/main/contracts/interfaces/INestBatchMining.sol#L228) | 
| Query unfreeze assets | [balanceOf](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/main/contracts/interfaces/INestBatchMining.sol#L218) |

