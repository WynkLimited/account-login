package com.wynk.utils;

import au.com.bytecode.opencsv.CSVReader;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.wynk.common.OriginPrefix;
import com.wynk.db.S3StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.wynk.common.OriginPrefix.SECURE_BUCKET;
import static com.wynk.common.OriginPrefix.UNSECURE_BUCKET;

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
