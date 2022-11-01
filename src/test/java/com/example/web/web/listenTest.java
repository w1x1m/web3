package com.example.web.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class listenTest {
    @Resource(name = "web3j")
    private Web3j web3;


    //token交易监听
    @Test
    public void listen1() {
        try {
            //获取最新的区块号
//            ListenInfo listen = listenInfoRepository.findFirstByEvent(ListenEventEnum.ROVER.getKey());
            BigInteger latestBloNum = web3.ethBlockNumber().send().getBlockNumber();

            //监听的交易合约事件
            Event event = new Event(
                    //要监听的事件
                    "Transfer",
                    Arrays.asList(
                            //参数类型
                            new TypeReference<Address>(true) {
                            },
                            new TypeReference<Address>(true) {
                            },
                            new TypeReference<Uint>() {
                            }
                    ));

            //合约地址
            String contracts = "0xd5Dfe09C5b35F896E6bF3C5CB1bbE676F8F368e8";
            /**
             * 过滤出要监听的内容
             * 1，指定区块号
             * 2.事件类型
             * 3.合约地址
             */
//            EthFilter filter = new EthFilter(
//                    DefaultBlockParameter.valueOf(latestBloNum),
//                    DefaultBlockParameterName.LATEST,
//                    contracts);
            /**
             * 过滤出二个区块之间的数据
             *  */
            BigInteger bigInteger = new BigInteger("20130300");
            EthFilter filter1 = new EthFilter(
                    //从那个区块开始查询
                    DefaultBlockParameter.valueOf(bigInteger),
                    //查询多少区块
                    DefaultBlockParameter.valueOf(bigInteger.add(new BigInteger("500"))),
                    //查询的合约地址
                    contracts);

            filter1.addSingleTopic(EventEncoder.encode(event));
            //监听符合条件的交易日志
            List<EthLog.LogResult> logs = web3.ethGetLogs(filter1).send().getLogs();
//            Request<?, EthTransaction> ethTransactionRequest = web3.ethGetTransactionByHash("0x219a0844c5d9fa8808571cc68b6b17d0d17bdfd8d20df5409eee02095e2b90c1");
//            web3.ethGetTransactionByHash()
            System.out.println("交易日志 = " + logs);

            for (EthLog.LogResult logResult : logs) {
                Log ethLog = (Log) logResult.get();
                //查询出交易双方地址和交易价格
                EventValues eventValues = Contract.staticExtractEventParameters(event, ethLog);
                List<Type> types = eventValues.getNonIndexedValues();
                Type type1 = eventValues.getIndexedValues().get(0);
                String to = type1.getValue().toString();
                System.out.println("发送地址 = " + to);
                Type type2 = eventValues.getIndexedValues().get(1);
                String from = type2.getValue().toString();
                System.out.println("接受地址 = " + from);
                for (Type type : types) {
                    String valus = type.getValue().toString();
                    System.out.println("交易价格 = " + valus);
                }
//                System.out.println("eventValues = " + eventValues);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //查询余额
    @Test
    public void getBalance() {
        //查询的钱包地址
        String from = "0xfa7c07263228130833e8698e0c6c601f9fc24288";
        //合约地址
        String contractAddress = "0xd5Dfe09C5b35F896E6bF3C5CB1bbE676F8F368e8";

        String methodName = "balanceOf";
        ArrayList<Type> inputParameters = new ArrayList<>();
        Address fromAddress = new Address(from);
        inputParameters.add(fromAddress);

        ArrayList<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(from, contractAddress, data);
        EthCall ethCall;
        BigDecimal zero = BigDecimal.ZERO;
        try {
            ethCall = web3.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            BigInteger value = new BigInteger("0");
            if (results != null && results.size() > 0) {
                String valueOf = String.valueOf(results.get(0).getValue());
                value = new BigInteger(valueOf);
            }
            System.out.println("剩余余额 =" + value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    //监听Burn销毁
    public void listen2() {
        try {
            BigInteger latestBloNum = web3.ethBlockNumber().send().getBlockNumber();


            //监听的交易合约事件
            Event event = new Event("Burn",
                    Arrays.asList(
                            new TypeReference<Address>(true) {
                            },
                            new TypeReference<Uint>() {
                            }
                    ));
            //合约地址
            String contracts = "0xd5Dfe09C5b35F896E6bF3C5CB1bbE676F8F368e8";

            BigInteger integer = new BigInteger("20134205");
            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(integer),
                    DefaultBlockParameter.valueOf(integer.add(new BigInteger("500"))),
//                    DefaultBlockParameterName.LATEST,
                    contracts);
            filter.addSingleTopic(EventEncoder.encode(event));
            List<EthLog.LogResult> logs1 = web3.ethGetLogs(filter).send().getLogs();
//            Request<?, EthLog> logs = (Request<?, EthLog>) logs1
            System.out.println("logs = " + logs1);

            for (EthLog.LogResult logResult : logs1) {
                Log eth = (Log) logResult.get();
                EventValues eventValues = Contract.staticExtractEventParameters(event, eth);
//                System.out.println("eventValues = " + eventValues);
                List<Type> types = eventValues.getNonIndexedValues();
                for (Type type : types) {
                    String string = type.getValue().toString();
                    System.out.println("销毁的token价值= " + string);
                    BigInteger bigInteger = new BigInteger(string);
//                    BigInteger[] bigIntegers = bigInteger.divideAndRemainder(BigInteger.valueOf(10).pow(18));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
