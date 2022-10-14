package com.example.fabric.controller;

import com.example.fabric.constant.ReplyMsg;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

/**
 * @author Zero
 */
@RestController
@RequestMapping("/ca")
public class CaController {

    @RequestMapping("/registerUser")
    public ReplyMsg registerUser(@RequestParam("userName") String userName) {
        try {
            String result = enrollAdmin();
            if (StringUtils.hasText(result)) {
                return ReplyMsg.retErrorMsg(result);
            }

            //check userName
            Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
            if (wallet.get(userName) != null) {
                return ReplyMsg.retErrorMsg("An identity for the user \" " + userName + " \" already exists in the wallet");
            }

            //read param
            Properties properties = new Properties();
            InputStream inputStream = CaController.class.getResourceAsStream("/application.properties");
            properties.load(inputStream);
            String caUrl = properties.getProperty("caUrl");
            String pemFile = properties.getProperty("pemFile");
            String mspId = properties.getProperty("mspId");
            String affiliation = properties.getProperty("affiliation");

            //build caClient
            Properties props = new Properties();
            props.put("pemFile", pemFile);
            props.put("allowAllHostNames", "true");
            HFCAClient caClient = HFCAClient.createNewInstance(caUrl, props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            X509Identity adminIdentity = (X509Identity) wallet.get("admin");
            if (adminIdentity == null) {
                return ReplyMsg.retErrorMsg("\"admin\" needs to be enrolled and added to the wallet first");
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
                    return affiliation;
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
                    return mspId;
                }

            };

            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest(userName);
            registrationRequest.setAffiliation(affiliation);
            registrationRequest.setEnrollmentID(userName);
            String enrollmentSecret = caClient.register(registrationRequest, user);
            Enrollment enrollment = caClient.enroll(userName, enrollmentSecret);
            Identity identity = Identities.newX509Identity(mspId, enrollment);
            wallet.put(userName, identity);

            return ReplyMsg.retSuccessMsg("Successfully enrolled user \" " + userName + " \" and imported it into the wallet");
        } catch (Exception e) {
            return ReplyMsg.retErrorMsg("RegisterUser " + e.getMessage());
        }
    }

    private String enrollAdmin() {
        try {
            //check admin
            Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
            if (wallet.get("admin") != null) {
                return null;
            }

            //read param
            Properties properties = new Properties();
            InputStream inputStream = CaController.class.getResourceAsStream("/application.properties");
            properties.load(inputStream);
            String caUrl = properties.getProperty("caUrl");
            String caHost = properties.getProperty("caHost");
            String pemFile = properties.getProperty("pemFile");
            String adminName = properties.getProperty("adminName");
            String adminSecret = properties.getProperty("adminSecret");
            String mspId = properties.getProperty("mspId");

            if (!StringUtils.hasText(caUrl) || !StringUtils.hasText(caHost) || !StringUtils.hasText(pemFile) || !StringUtils.hasText(adminName) || !StringUtils.hasText(adminSecret) || !StringUtils.hasText(mspId)) {
                return "EnrollAdmin Please check params";
            }

            //build caClient
            Properties props = new Properties();
            props.put("pemFile", pemFile);
            props.put("allowAllHostNames", "true");
            HFCAClient caClient = HFCAClient.createNewInstance(caUrl, props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Enroll the admin user, and import the new identity into the wallet.
            final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost(caHost);
            enrollmentRequestTLS.setProfile("tls");
            Enrollment enrollment = caClient.enroll(adminName, adminSecret, enrollmentRequestTLS);
            Identity userAdmin = Identities.newX509Identity(mspId, enrollment);
            wallet.put("admin", userAdmin);
            return null;
        } catch (Exception e) {
            return "EnrollAdmin " + e.getMessage();
        }
    }

}
