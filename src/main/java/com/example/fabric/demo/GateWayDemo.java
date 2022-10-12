package com.example.fabric.demo;

import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;


/**
 * @author zero
 */
public class GateWayDemo {
    public static void main(String[] args) {
        try {
            //获取相应参数
            Properties properties = new Properties();
            InputStream inputStream = GateWayDemo.class.getResourceAsStream("/application.properties");
            properties.load(inputStream);

            String networkConfigPath = properties.getProperty("networkConfigPath");
            String channelName = properties.getProperty("channelName");
            String contractName = properties.getProperty("contractName");
            //使用org1中的user1初始化一个网关wallet账户用于连接网络
            String certificatePath = properties.getProperty("certificatePath");
            X509Certificate certificate = readX509Certificate(Paths.get(certificatePath));

            String privateKeyPath = properties.getProperty("privateKeyPath");
            PrivateKey privateKey = getPrivateKey(Paths.get(privateKeyPath));

            Wallet wallet = Wallets.newInMemoryWallet();
            wallet.put("user1",Identities.newX509Identity("Org1MSP",certificate,privateKey));

            //根据connection.json 获取Fabric网络连接对象
            Gateway.Builder builder = Gateway.createBuilder()
                    .identity(wallet, "user1")
                    .networkConfig(Paths.get(networkConfigPath));
            //连接网关
            Gateway gateway = builder.connect();
            //获取通道
            Network network = gateway.getNetwork(channelName);
            Channel channel = network.getChannel().initialize();
            //获取合约对象
            Contract contract = network.getContract(contractName);
            //查询现有资产
            //注意更换调用链码的具体函数
            byte[] queryAllAssets = contract.evaluateTransaction("GetAllAssets");
            System.out.println("所有资产："+new String(queryAllAssets, StandardCharsets.UTF_8));

//            try {
//                byte[] invokeResult = contract.createTransaction("CreateAsset")
//                        .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
//                        .submit("asset7", "blue", "18", "Zero", "666");
//                System.out.println(new String(invokeResult, StandardCharsets.UTF_8));
//            } catch (Exception e) {
//                System.err.println("Expected an error on CreateAsset: " + e);
//            }

//            byte[] updateResult = contract.submitTransaction("UpdateAsset","asset1", "blue", "5", "Tomoko", "350");
//            System.out.println("result: " + new String(updateResult));
//            byte[] queryAllAssetsAfter = contract.evaluateTransaction("GetAllAssets");
//            System.out.println("更新资产："+new String(queryAllAssetsAfter, StandardCharsets.UTF_8));

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }
}
