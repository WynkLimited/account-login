package com.wynk.utils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by a1vlqlyy on 23/01/17.
 */
@Component
public class S3Utils {

    private final Logger logger = LoggerFactory.getLogger(S3Utils.class.getCanonicalName());
    private static AWSCredentialsProvider credentialsProvider;

    public static AWSCredentialsProvider getAWSCreds() {
        try {
            if(credentialsProvider == null) {
                credentialsProvider = new DefaultAWSCredentialsProviderChain();
            }
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                "Cannot load the credentials from the credential profiles file", e);
        }
        return credentialsProvider;
    }
}
