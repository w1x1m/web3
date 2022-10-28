package com.example.web.web;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class transactionTest {
    @Resource(name = "web3j")
    private  Web3j web3;

    @Test
    public void transactionERC20Test1(){
        // 转出地址
        String from = "0x86438Bb559313323755219f067e67c19EADEB3a4";
        //转入地址
        String to = "0xFA7c07263228130833E8698e0c6c601f9Fc24288";
        //转入数量
        String value = "1";
        //转出地址私钥
        String privateKey ="6bfc01c36009cd417423c9fce07e89ece98791801cec6d51cf27a1edec339a80";
        //合约地址
        String contractAddress="0x60d64Ef311a4F0E288120543A14e7f90E76304c6";
        //位数，根据合约里面的来
        int decimal=18;
        tokenDeal(from,to,value,privateKey,contractAddress,decimal);

    }

    public  String tokenDeal(String from, String to, String value, String privateKey, String contractAddress, int decimal) {
        try {
            Web3j web3j = Web3j.build(new HttpService("https://http-testnet.hecochain.com"));
            //转账的凭证，需要传入私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取交易笔数
            BigInteger nonce;
            //获取交易信息
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
            if (ethGetTransactionCount == null) {
                return null;
            }
            //获取交易笔数
            nonce = ethGetTransactionCount.getTransactionCount();
            //手续费
            BigInteger gasPrice;
            EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
            if (ethGasPrice == null) {
                return null;
            }
            gasPrice = ethGasPrice.getGasPrice();
            //注意手续费的设置，这块很容易遇到问题
            BigInteger gasLimit = BigInteger.valueOf(60000000000L);

            BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(decimal)).toBigInteger();// 单位换算
            Function function = new Function(
                    "transfer",
                    Arrays.asList(new Address(to), new Uint256(val)),
                    Collections.singletonList(new TypeReference<Type>() {
                    }));
            //创建交易对象
            String encodedFunction = FunctionEncoder.encode(function);
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit,
                    contractAddress, encodedFunction);

            //进行签名操作
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);
            //发起交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String hash = ethSendTransaction.getTransactionHash();
            if (hash != null) {
                //执行业务
                System.out.printf("执行成功：" + hash);
                return hash;
            }
        } catch (Exception ex) {
            //报错应进行错误处理
            ex.printStackTrace();
        }
        return null;

    }

    @Test
    public void transactionERC20Test2() {
        try {
        Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed1.binance.org/"));
        Credentials credentials = Credentials.create("6bfc01c36009cd417423c9fce07e89ece98791801cec6d51cf27a1edec339a80");

        String fromAddress = credentials.getAddress();
        String toAddress = "0xFA7c07263228130833E8698e0c6c601f9Fc24288";

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();


        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(1)),
                Arrays.asList(new TypeReference<Type>() {
                }));

        String encodedFunction = FunctionEncoder.encode(function);
            String version = web3j.netVersion().send().getNetVersion();
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, Convert.toWei("0.1", Convert.Unit.GWEI).toBigInteger(),
                BigInteger.valueOf(300000000000L), "0x363aAA22c3133037CcfA91f50AfcC41191F661b5",encodedFunction);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println(transactionHash);
        } catch (Exception ex) {
            //报错应进行错误处理
            System.out.println("\"\" = " + "报错");
            ex.printStackTrace();

        }
    }
}
