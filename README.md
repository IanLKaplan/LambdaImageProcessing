# LambdaImageProcessing
A set of Amazon Lambda Web Services Java functions to scale images, using the Java imgscalr library

Amazon Web Services (AWS) Lambda allows functions that use significant memory and processing time to be off-loaded to an AWS Lambda function.

Moving functionality to an AWS Lambda function allows a web application to use less EC2 dedicated processing power and improves scalability.

As this code demonstrates, Lambda functions can be used like remote procedure calls (in this case, blocking remote procedure calls). A class function, serialized to a JSON String, is passed the parameters needed to scale an image.
This code is written to process an unscaled image that has been stored on Amazon S3 storage. The unscaled iamge is read, scaled and then written to an S3 path defined in the argument.

This code was originally written to handle image processing for images posted to the board or image galleries of nderground (www.nderground.net), a social network designed for privacy.

AWS Lambda code can only be debugged by outputting log file messages and examining the resulting log files. This made testing difficult, so it is important that Lambda code be extensively tested before it is deployed. This code includes both local and Lambda call test code. To use this test code you will need to use your own Amazon ID and Key.

This code was written by Ian Kaplan, the founder of Topstone Software consulting (www.topstonesoftware.com). Ian Kaplan also designed and built the nderground social network. Topstone Software Consulting provides consulting services in a variety of areas, including large scale web application design and development, Amazon Web Services application architecture, Web security abd machine learning.
