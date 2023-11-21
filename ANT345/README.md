# ANT345 - Simplify working with data across multicloud with AWS analytics

In this session, we look at how you can use AWS analytic services to do in-place queries of data that lies outside of AWS. Specifically, we look at how you can use Amazon Athena to do ad-hoc queries of data in Snowflake and Google BigQuery, and how you can use AWS Glue to create a job that joins data from Snowflake, Google BigQuery, and Azure Data Lake storage. 

## Pre-requisites
Durng the session we show you how to configure the Amazon Athena and AWS Glue connectors for various data sources. However, before doing that configuration, there are some required pre-requisites, which are not shown during the session due to time constraints. Below we cover setting up the following pre-requisites. 

1. Creating Amazon Secret Manager secrets to store credentials for your data sources
2. Creating Amazon S3 buckets to be used by the Amazon Athena connectors for temporary storage
3. Creating an Amazon S3 bucket to store Amazon Athena query results

### Creating Amazon Secret Manager secrets
Both the Amazon Athena and AWS Glue connectors make use of AWS Secrets Manager to store credentials securely. 

#### Configuring secrets for Snowflake
In this session we used Amazon Athena and AWS Glue to connect to Snowflake. However, we need different secrets for each AWS service, as the configuration for each is slightly different.

##### Configuring a secret for Amazon Athena to connect to Snowflake
Let's start by creating the secret that Amazon Athena will use to connect to Snowflake.

1.	Login to the **AWS Management Console**, and search for and open **Secrets Manager**
2.	On the Secrets Manager console, choose **Secrets** 
3.	Choose **Store a new secret**.
4.	For **Secret type**, select **Other types of secret**.
5.	Enter your Snowflake credentials as the following key-value pairs (click +Add row to add extra values). Choose Next.

Key = `username`, Value = `your_snowflake_username`

Key = `password`, Value = `your_snowflake_password`

6. Click **Next**, and then provide a **Secret name** (such as `athena/snowflake`), and optionally a **Description**. Then click **Next**
7. Leave the remaining fields at their defaults, and choose **Next**.
8. Select **Store** to save your secret

##### Configuring a secret for AWS Glue to connect to Snowflake
In a similair way, we can store the secret that is needed for AWS Glue to connect to Snowflake. The process is the same as for the Athena secret outlined above, except that the key names are different. 

1. Repeat the Steps described for connecting Amazon Athena to Snowflake, except for Step 5, change to use the following key / value paris.

Key = `sfUser`, Value = `your_snowflake_username`

Key = `sfPassword`, Value = `your_snowflake_password`
   
2. In Step 6, use a **Secret name** such as `glue/snowflake`)

#### Configuring secrets for Google BigQuery
Let's now configure the secrets needed to connect to Google BigQuery, using both Amazon Athena and AWS Glue. 

##### Configuring a secret for Amazon Athena to connect to Google BigQuery
For Amazon Athena to connect to Google BigQuery, we need to provide our Google Cloud service account credentials JSON file in plain text to Secrets Manager. Amazon Secrets Manager will ensure that the credentials are encrypted. 

1.	Login to the **AWS Management Console**, and search for and open **Secrets Manager**
2.	On the Secrets Manager console, choose **Secrets** 
3.	Choose **Store a new secret**.
4.	For **Secret type**, select **Other types of secret**.
5.	Under **Key/Value pairs**, change to the **Plaintext** tab, and then copy and paste your Google service account credentials JSON into the text box. 
6. Click **Next**, and then provide a **Secret name** (such as `athena/bigquery`), and optionally a **Description**. Then click **Next**
7. Leave the remaining fields at their defaults, and choose **Next**.
8. Select **Store** to save your secret

##### Configuring a secret for AWS Glue to connect to Google BigQuery
In a similair way to connect to Google BigQuery from AWS Glue,you will need to create and store your Google Cloud Platform credentials in a AWS Secrets Manager secret, then associate that secret with a Google BigQuery AWS Glue connection.

1. Download the  Google Cloud service account credentials JSON file.
2. base64 encode your downloaded credentials file. On an AWS CloudShell session or similar, you can do this from the command line by running cat credentialsFile.json | base64 -w 0. Retain the output of this command, credentialString.
3. In AWS Secrets Manager, create a secret using your Google Cloud Platform credentials. To create a secret in Secrets Manager, follow the steps provided above section.When selecting Key/value pairs, create a pair for the key credentials with the value credentialString.









