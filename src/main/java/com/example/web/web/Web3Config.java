package com.example.web.web;


import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.TimeUnit;


@Configuration
public class Web3Config {

    @Value("${web3.listen.url}")
    private String listenUrl;

    @Value("${web3.rebate.url}")
    private String rebateUrl;

    @Bean(name = "web3j")
    public Web3j web3j() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);

        OkHttpClient httpClient = builder.build();
        return Web3j.build(new HttpService(listenUrl,httpClient));

    }

    @Bean(name = "web3Rebate")
    public Web3j web3Rebate() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);
        OkHttpClient httpClient = builder.build();
        return Web3j.build(new HttpService(rebateUrl,httpClient));

    }
}
