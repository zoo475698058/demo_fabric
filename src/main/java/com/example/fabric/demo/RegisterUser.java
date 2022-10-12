package com.example.fabric.demo;/*
SPDX-License-Identifier: Apache-2.0
*/

import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

public class RegisterUser {

	public static void main(String[] args) throws Exception {

		String userName = "appUser2";

		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put("pemFile", "./wallet/org1.example.com/ca/ca.org1.example.com-cert.pem");
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("https://192.168.2.238:7054", props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

		// Check to see if we've already enrolled the user.
		if (wallet.get(userName) != null) {
			System.out.println("An identity for the user \" "+userName+" \" already exists in the wallet");
			return;
		}

		X509Identity adminIdentity = (X509Identity)wallet.get("admin");
		if (adminIdentity == null) {
			System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
			return;
		}
		User user = new User() {

			@Override
			public String getName() {
				return userName;
			}

			@Override
			public Set<String> getRoles() {
				return null;
			}

			@Override
			public String getAccount() {
				return null;
			}

			@Override
			public String getAffiliation() {
				return "org1.department1";
			}

			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {

					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}

					@Override
					public String getCert() {
						return Identities.toPemString(adminIdentity.getCertificate());
					}
				};
			}

			@Override
			public String getMspId() {
				return "Org1MSP";
			}

		};

		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest(userName);
		registrationRequest.setAffiliation("org1.department1");
		registrationRequest.setEnrollmentID(userName);
		String enrollmentSecret = caClient.register(registrationRequest, user);
		Enrollment enrollment = caClient.enroll(userName, enrollmentSecret);
		Identity identity = Identities.newX509Identity("Org1MSP", enrollment);
		wallet.put(userName, identity);
		System.out.println("Successfully enrolled user \" "+userName+" \" and imported it into the wallet");
	}

}
