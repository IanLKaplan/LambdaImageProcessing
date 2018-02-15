/** \file
 * 
 * Jan 3, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package com.amazonaws.lambda.nderground;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

/**
 * <h3>
 * AmazonCredentials
 * </h3>
 * <p>
 * A little class to build AWS credentials
 * </p>
 * Jan 3, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class AmazonCredentials {
    
    public static AWSCredentials createNewCredentials(String AWS_ID, String AWS_KEY) {
        AWSCredentials credentials = new BasicAWSCredentials( AWS_ID, AWS_KEY );
        return credentials;
    }

}
