package com.example.web.web;


import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@RunWith(SpringRunner.class)
class boboweb {

    @Resource(name = "web3j")
    private Web3j web3;

    @Test
    //测试连接链是否成功
    public void w() throws IOException, ExecutionException, InterruptedException {

        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        //获取块号
        BigInteger latestBloNum = web3.ethBlockNumber().send().getBlockNumber();
        System.out.println("latestBloNum = " + latestBloNum);
//        WalletUtils.loadCredentials(,)
    }

    //生成钱包地址
    @Test
    public void download() {
        try {
            Integer num = 10;
            ApplicationHome home = new ApplicationHome(getClass());

            String tempPath = home.getDir().getParentFile().getPath();
//        String tempPath =new String("D:\\java\\idea\\ideawj\\web\\target") ;

            int i = 0;
            while (i < num) {
                /**
                 *generateBip39Wallet = 生成一个带注记词的钱包地址
                 * filename = 钱包地址
                 * mnemonic = 注记词
                 */
                Bip39Wallet wallet = WalletUtils.generateBip39Wallet("", new File(tempPath));
                //获取注记词
                String memorizingWords = wallet.getMnemonic();
                Credentials credentials = WalletUtils.loadBip39Credentials("",
                        wallet.getMnemonic());
                String address = credentials.getAddress();
                //获取私钥
                String priKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                if (!WalletUtils.isValidPrivateKey(priKey)) {
                    FileUtil.del(new File(tempPath + File.separator + wallet.getFilename()));
                    continue;
                }
                i++;
            }
        } catch (IOException | CipherException ex) {
            ex.printStackTrace();
        }


    }

    //查询代币余额
    @Test
    public void x() throws Exception {
        ApplicationHome home = new ApplicationHome(getClass());
        String tempPath = home.getDir().getParentFile().getPath();
        //设置gas费
        BigInteger GAS_LIMIT = BigInteger.valueOf(9_000_000);
        BigInteger GAS_PRICE = BigInteger.valueOf(4_100_000_000L);
        //创建一个钱包
//        Bip39Wallet wallet = WalletUtils.generateBip39Wallet("", new File(tempPath));
        //获取凭证
//        Credentials credentials = WalletUtils.loadBip39Credentials("qwer@xjb", wallet.getMnemonic());
//        Credentials credentials = WalletUtils.loadCredentials("qwer@xjb", wallet.getFilename());
        //转账人私钥
//        Credentials credensstials = Credentials.create("xxxxxxxxxxxxx");

//        TransactionReceipt t = Transfer.sendFunds(web3, credentials, "0xFA7c07263228130833E8698e0c6c601f9Fc24288", BigDecimal.valueOf(1), Convert.Unit.WEI).send();
//        System.out.println("t = " + t);

        //获取以太余额
        BigInteger balance = web3.ethGetBalance("0xFA7c07263228130833E8698e0c6c601f9Fc24288", DefaultBlockParameterName.LATEST).send().getBalance();
        System.out.println("balance = " + balance);
//        Credentials c1 = Credentials.create("6bfc01c36009cd417423c9fce07e89ece98791801cec6d51cf27a1edec339a80");

        //查询的钱包地址
        String from = "0x86438Bb559313323755219f067e67c19EADEB3a4";
        //合约地址
        String contractAddress = "0x60d64Ef311a4F0E288120543A14e7f90E76304c6";
        //查询以太余额
        String code = getERC20Balance(web3, from, contractAddress);
        System.out.printf("查询出来的余额：" + code);

    }

    private static final BigDecimal WEI = new BigDecimal(1);

    /**
     * 获取ERC-20 token指定地址余额
     *
     * @param address         查询地址
     * @param contractAddress 合约地址
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String getERC20Balance(Web3j web3j, String address, String contractAddress) throws ExecutionException, InterruptedException {
        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address fromAddress = new Address(address);
        inputParameters.add(fromAddress);
        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(address, contractAddress, data);
        EthCall ethCall;
        BigDecimal balanceValue = BigDecimal.ZERO;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            String value = "";
            if (results != null && results.size() > 0) {
                value = String.valueOf(results.get(0).getValue());
            }
//            return value;
            balanceValue = new BigDecimal(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue.toString();

//     return null;
    }


}
