### NEST4.0 BSC自动报价程序操作说明

[toc]


#### 介绍
>主流资产报价，采用nest 或者ntoken的方式都是合理的，因为已经存在的资产可以通过社区博弈实现有效的激励，但一些长尾资产靠ntoken是很难启动的，因此我们从项目方的角度，推出一种新的模型：由项目团队或者用户来设置激励、调用等经济模型，给自己的代币报价并挖出自己的代币！同时调用费由自己安排和分配！


>NEST4.0自动报价程序是一个示例程序，无法直接使用，需要根据本程序代码进行二次开发，开发中遇到任何问题，均可在github上提交问题，本程序开发人员会一一解答。

>本程序相关参数默认值如区块间隔、报价gasPrice倍数等并不是最优的策略方案，用户可根据实际情况进行调整。

>自动报价程序主要功能有：
   * 检查账户资产、解冻资产、冻结资产情况。
   * ERC20代币授权。
   * 发起报价（post报价）。
   * 取消报价（自动判断合约最新报价区块号是否改变，如果最新区块号大于自己报价时区块号，则取消报价，避免被卡区块）。
   * 关闭报价单，解冻报价资产。
   * 取出合约内解冻资产。
   * 重新获取合约参数。

#### 启动前准备

1. 实现价格接口[PriceService](https://github.com/NEST-Protocol/NEST-Oracle-V4.0-minner/blob/bsc/src/main/java/com/nest/ib/service/PriceService.java)。
   * 获取token0Token1的价格，即1个token0价值多少个token1

2. 准备好：钱包私钥及相关资产、节点。
   * 钱包私钥：
   通过助记词生成，可通过nestDapp注册。
   * 需要资产:
   <br/>token0、token1以及报价需要抵押的NEST，每笔报价需抵押10万NEST。
   <br/>发送交易的gas消耗（BNB）
   * BSC链节点。

#### 启动和关闭

1. 部署运行报价程序：建议使用https协议。
2. 登录：
   * 浏览器输入http://127.0.0.1:8088/main，会进入登录页面，默认用户名nest，密码nestqwe123!。
   * 如需修改密码，可修改src/main/resources/application.yml中的login.name（用户名）、login.passwd（密码）。
3. 关闭报价程序：
   * 关闭报价程序前先停止挖矿，然后等待10分钟，待报价资产确认解冻完毕后再关闭窗口。

#### 相关设置

1. 节点设置（必填）：
   * 必须优先设置节点地址。
2. 设置报价通道ID
3. 报价通道ID确定后，点击`confirm`按钮，后台日志会打印TOKEN0和TOKEN0的代币信息，检查信息无误后继续后续操作。
4. 设置报价私钥（必填）：
   * 填写私钥，程序会进行授权检查，如果未授权，程序会自动发起授权交易，请确定授权交易打包成功后方可进行报价。
5. gasPrice 倍数配置（在默认gasPrice基础上进行加倍，可根据实际情况进行调整）。
6. 开启挖矿：
   * 以上配置完成后，便可开启挖矿，可以在后台日志查看报价时区块高度、报价数量、报价交易hash等信息。
7. 取出资产
   * 只有关闭的报价单资产才能取出，如需取出所有资产，请关闭所有报价单后再进行取出操作。

#### 涉及合约接口@BSC
| 功能 | 接口                                                                                                                      | 
| ---- |-------------------------------------------------------------------------------------------------------------------------|
| 报价 | [post](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/bsc/contracts/interfaces/INestBatchMining.sol#L182)       |
| 获取报价单 | [list](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/bsc/contracts/interfaces/INestBatchMining.sol#L200)      | 
| 关闭报价单 | [close](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/bsc/contracts/interfaces/INestBatchMining.sol#L212)     | 
| 取出资产 | [withdraw](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/bsc/contracts/interfaces/INestBatchMining.sol#L223)  | 
| 预估出矿量 | [estimate](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/bsc/contracts/interfaces/INestBatchMining.sol#L228)  | 
| 查询解冻资产 | [balanceOf](https://github.com/NEST-Protocol/NEST-Oracle-V4.0/blob/bsc/contracts/interfaces/INestBatchMining.sol#L218) |


