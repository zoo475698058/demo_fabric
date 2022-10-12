package com.example.fabric.demo;/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

import org.hyperledger.fabric.gateway.*;

import java.nio.file.Path;
import java.nio.file.Paths;


public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "false");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("wallet", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
//		try {
//			EnrollAdmin.main(null);
//			RegisterUser.main(null);
//		} catch (Exception e) {
//			System.err.println(e);
//		}

//		testContractAsset();
		testContractERC721();
		testContractERC20();


	}

	private static void testContractERC20() {
		try (Gateway gateway = connect()) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("token_erc20");

			System.out.println("----------------------------------testContractERC20---------------------");
			String user1Address = "x509::CN=appUser, OU=org1 + OU=client + OU=department1::CN=ca.org1.example.com, O=org1.example.com, L=Durham, ST=North Carolina, C=US";
			String user2Address = "x509::CN=appUser2, OU=org1 + OU=client + OU=department1::CN=ca.org1.example.com, O=org1.example.com, L=Durham, ST=North Carolina, C=US";

			byte[] result;

//			result = contract.submitTransaction("Initialize", "xhzf coin", "XB", "0");
//			System.out.println("Initialize, result: " + new String(result));
//
			result = contract.evaluateTransaction("TokenName");
			System.out.println("TokenName, result: " + new String(result));

			result = contract.evaluateTransaction("TokenSymbol");
			System.out.println("TokenSymbol, result: " + new String(result));

			result = contract.evaluateTransaction("Decimals");
			System.out.println("Decimals, result: " + new String(result));

//			result = contract.submitTransaction("Mint", "200000000");
//			System.out.println("MintWithTokenURI, result: " + new String(result));

//			result = contract.submitTransaction("Burn", "10000000");
//			System.out.println("Burn, result: " + new String(result));

//			result = contract.evaluateTransaction("TotalSupply");
//			System.out.println("TotalSupply, result: " + new String(result));

//			result = contract.submitTransaction("Approve", user1Address, "80000000");
//			System.out.println("Approve, result: " + new String(result));
//
//			result = contract.evaluateTransaction("Allowance", user1Address, user1Address);
//			System.out.println("Allowance, result: " + new String(result));
//
//			result = contract.submitTransaction("TransferFrom", user1Address, user2Address, "50000000");
//			System.out.println("TransferFrom, result: " + new String(result));
//
//			result = contract.evaluateTransaction("Allowance", user1Address, user1Address);
//			System.out.println("Allowance, result: " + new String(result));

			result = contract.evaluateTransaction("ClientAccountBalance");
			System.out.println("ClientAccountBalance, result: " + new String(result));

			result = contract.evaluateTransaction("ClientAccountID");
			System.out.println("ClientAccountID, result: " + new String(result));

			result = contract.evaluateTransaction("BalanceOf", user1Address);
			System.out.println("BalanceOf, result: " + new String(result));

			result = contract.evaluateTransaction("BalanceOf", user2Address);
			System.out.println("BalanceOf, result: " + new String(result));

		}
		catch(Exception e){
			System.err.println(e);
		}
	}

	private static void testContractERC721() {
		try (Gateway gateway = connect()) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("token_erc721");

			System.out.println("----------------------------------testContractERC721---------------------");
			String user1Address = "x509::/OU=org1/OU=client/OU=department1/CN=appUser::/C=US/ST=North Carolina/L=Durham/O=org1.example.com/CN=ca.org1.example.com";
			String user2Address = "x509::/OU=org1/OU=client/OU=department1/CN=appUser2::/C=US/ST=North Carolina/L=Durham/O=org1.example.com/CN=ca.org1.example.com";

			byte[] result;

//			result = contract.submitTransaction("Initialize", "xhzflogo", "xhlogo");
//			System.out.println("Initialize, result: " + new String(result));

//			result = contract.submitTransaction("MintWithTokenURI", "1", "https://cdn.xhj.com/static/web/images/index/logo.png");
//			System.out.println("MintWithTokenURI, result: " + new String(result));

//			result = contract.submitTransaction("MintWithTokenURI", "2", "https://szimg.xhj.com/erweima/xhj_app.png");
//			System.out.println("MintWithTokenURI, result: " + new String(result));

			result = contract.evaluateTransaction("Name");
			System.out.println("Name, result: " + new String(result));

			result = contract.evaluateTransaction("Symbol");
			System.out.println("Symbol, result: " + new String(result));

//			result = contract.submitTransaction("Burn", "1");
//			System.out.println("Burn, result: " + new String(result));

			result = contract.evaluateTransaction("TotalSupply");
			System.out.println("TotalSupply, result: " + new String(result));

//			result = contract.evaluateTransaction("TokenURI", "1");
//			System.out.println("TokenURI, result: " + new String(result));
//
//			result = contract.evaluateTransaction("OwnerOf", "1");
//			System.out.println("OwnerOf, result: " + new String(result));

//			result = contract.submitTransaction("Approve", user1Address, "1");
//			System.out.println("Approve, result: " + new String(result));

			result = contract.evaluateTransaction("GetApproved", "1");
			System.out.println("GetApproved, result: " + new String(result));

//			result = contract.submitTransaction("TransferFrom", user1Address, user2Address, "1");
//			System.out.println("TransferFrom, result: " + new String(result));

			result = contract.evaluateTransaction("ClientAccountBalance");
			System.out.println("ClientAccountBalance, result: " + new String(result));

			result = contract.evaluateTransaction("ClientAccountID");
			System.out.println("ClientAccountID, result: " + new String(result));

			result = contract.evaluateTransaction("BalanceOf", user1Address);
			System.out.println("BalanceOf, result: " + new String(result));

			result = contract.evaluateTransaction("BalanceOf", user2Address);
			System.out.println("BalanceOf, result: " + new String(result));

		}
		catch(Exception e){
			System.err.println(e);
		}
	}

	private static void testContractAsset() {
		try (Gateway gateway = connect()) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("basic");

			byte[] result;

//			contract.submitTransaction("InitLedger");
//			contract.submitTransaction("CreateAsset", "asset15", "yellow", "15", "Zero", "1400");

			result = contract.evaluateTransaction("GetAllAssets");
			System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));

//			result = contract.evaluateTransaction("ReadAsset", "asset13");
//			System.out.println("result: " + new String(result));
//
			result = contract.evaluateTransaction("AssetExists", "asset1");
			System.out.println("result: " + new String(result));

			contract.submitTransaction("UpdateAsset", "asset1", "blue", "5", "Tomoko", "350");

			result = contract.evaluateTransaction("ReadAsset", "asset1");
			System.out.println("result: " + new String(result));

			try {
				System.out.println("\n");
				System.out.println("Submit Transaction: UpdateAsset asset70");
				contract.submitTransaction("UpdateAsset", "asset70", "blue", "5", "Tomoko", "300");
			} catch (Exception e) {
				System.err.println("Expected an error on UpdateAsset of non-existing Asset: " + e);
			}

			System.out.println("Submit Transaction: TransferAsset asset1 from owner Tomoko > owner Tom");
			// TransferAsset transfers an asset with given ID to new owner Tom
			contract.submitTransaction("TransferAsset", "asset1", "Tom");

			result = contract.evaluateTransaction("ReadAsset", "asset1");
			System.out.println("result: " + new String(result));
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
}
