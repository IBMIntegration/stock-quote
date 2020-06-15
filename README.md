#  Trader Lite - Stock Quote Service

The IBM Trader Lite application is a simple stock trading sample where you can create various stock portfolios and add shares of stock to each for a commission. The app is used to illustrate concepts in  the IBM Cloud Pak for Integration workshop and the App Modernization workshop taught by IBM Client Developer Advocacy.

![Architectural Diagram](architecture.png)

The **stock-quote** microservice is an Open Liberty app that sretrives quotes from an IBM API Connect proxy for an external Stock Quote service. This microservice:

   * retrieves a list of quotes when called with a comma separated list of ticket symbols
   * returns the DJIA


## Building the Stock Quote Service

This is an Open Liberty  app built and tested with OpenJDK 1.8. Enter the following command from the root of the repo to build it:

```
mvn clean package
```

An Open Liberty *war* will be built in the **target** folder.

The included [Dockerfile](Dockerfile) can be used to create an image for deployment to Kubernetes.

## Deploying the Stock Quote  Service

Refer to the IBM Cloud Pak for Integration workshop instructions or the  App Modernization workshop instructions on how this service is deployed as part of the Trader Lite app.
