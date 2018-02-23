# LambdaImageProcessing
A set of Amazon Lambda Web Services Java functions to scale images, using the Java imgscalr library

Amazon Web Services (AWS) Lambda allows functions that use significant memory and processing time to be off-loaded to an AWS Lambda function.

Moving functionality to an AWS Lambda function allows a web application to use less EC2 dedicated processing power and improves scalability.

As this code demonstrates, Lambda functions can be used like remote procedure calls (in this case, blocking remote procedure calls). A class function, serialized to a JSON String, is passed the parameters needed to scale an image.
This code is written to process an unscaled image that has been stored on Amazon S3 storage. The unscaled iamge is read, scaled and then written to an S3 path defined in the argument.

This code was originally written to handle image processing for images posted to the board or image galleries of nderground (www.nderground.net), a social network designed for privacy.

AWS Lambda code can only be debugged by outputting log file messages and examining the resulting log files. This made testing difficult, so it is important that Lambda code be extensively tested before it is deployed. This code includes both local and Lambda call test code. To use this test code you will need to use your own Amazon ID and Key.

# Code Structure Overview

The image processing code is designed to read images that have already been written to Amazon Web Services S3 storage. These
images are scaled and the result is written back to S3.

## lambda_jar

This package includes the core image processing software that reads images from AWS S3 and writes the scaled image to S3. 

### ImageService.java

The image processing functions supplied by this class read from a Java InputStream object and return a BufferedImage. 
This code can be used and tested outside of the Amazon Web Services environment.

### S3Service.java

This class provides function that built a Java InputStream object for images stored on AWS S3. Other functions will write
a BufferedImage object to S3 storage.

### LambdaImageScalar.java

This class integerates the image processing code and the S3 read/write code. The static functions provided by this class
are the functions that will be called with the image scaling information objects (ScaleImageByHeight, ScaleImageByHeightWidth,
ScaleImageByMaxDim).

### AbstractLoggerInterface.java

This Java Interface abstracts the logger used by the image processing code in the lambda_jar project. This allows the code
to be tested on both the local system and on Lambda.

## scale_image

This project contains the Lambda functions that will be downloaded to AWS Lambda to support Lambda image processing.

This code was written by Ian Kaplan, the founder of Topstone Software consulting (www.topstonesoftware.com). Ian Kaplan also designed and built the nderground social network. Topstone Software Consulting provides consulting services in a variety of areas, including large scale web application design and development, Amazon Web Services application architecture, Web security abd machine learning.

This code was built with Eclipse Luna, with the Amazon Lambda development plugin.
