
/*
       Copyright 2020 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ibm.hybrid.cloud.sample.stocktrader.stockquote;

import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

//Logging (JSR 47)
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.hybrid.cloud.sample.stocktrader.stockquote.json.Quote;
import com.ibm.hybrid.cloud.sample.stocktrader.stockquote.json.MarketSummary;


@ApplicationPath("/")
@Path("/")
public class StockQuote extends Application {

    private static Logger logger = Logger.getLogger(StockQuote.class.getName());
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static Client client = null;
    private static final String API_CONNECT_CLIENT_ID_ENV_VAR = "API_CONNECT_CLIENT_ID";
    private static String API_CONNECT_CLIENT_ID = null;

    // Disable  SSL validation so we can handle any  self signed certs in API Connect
    static {

      API_CONNECT_CLIENT_ID = System.getenv(API_CONNECT_CLIENT_ID_ENV_VAR);
      if (API_CONNECT_CLIENT_ID != null) {
          logger.info("Found API Connect Client ID in env");
      }
      else {
         API_CONNECT_CLIENT_ID = "Unauthorized";
        logger.log(Level.SEVERE, "Didn't find API Connect Client ID in env - all REST calls  to external API will fail !");
      }

  		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
  			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
  				return null;
  			}

  			public void checkClientTrusted(X509Certificate[] certs, String authType) {
  			}

  			public void checkServerTrusted(X509Certificate[] certs, String authType) {
  			}
  		} };

  		// Install the all-trusting trust manager

  		SSLContext sc = null;
  		try {
  			sc = SSLContext.getInstance("SSL");
  			sc.init(null, trustAllCerts, new java.security.SecureRandom());
  		} catch (NoSuchAlgorithmException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (KeyManagementException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}

  		//HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

  		// Create all-trusting host name verifier
  		HostnameVerifier allHostsValid = new HostnameVerifier() {
  			public boolean verify(String hostname, SSLSession session) {
  				return true;
  			}
  		};

  		// Install the all-trusting host verifier
      ClientBuilder clientBuilder =  ClientBuilder.newBuilder();
      clientBuilder.sslContext(sc);
      clientBuilder.hostnameVerifier(allHostsValid);

      client = clientBuilder.build();

  	}

    /* Get quotes for the comma separated list of symbols */
    @GET
	  @Path("/")
	  @Produces("application/json")
    public List<Quote> getQuotes(@QueryParam("symbols") String symbols) {


       String url =  System.getenv("API_CONNECT_PROXY_URL") + "/stocks/stock-quote?symbols=" + symbols;

       logger.info("Using Stock Quote URL from env: " + url);
       logger.info("Quotes requested for symbols: " + symbols);


       //Client client = ClientBuilder.newClient();

       WebTarget target = client.target(url);
       Response response = target.request().header("X-IBM-Client-Id", API_CONNECT_CLIENT_ID).get();


       String json = response.readEntity(String.class);

       response.close();

       return jsonb.fromJson(json, new ArrayList<Quote>(){}.getClass().getGenericSuperclass());

    }

    /* Get the latest DJIA */
    @GET
	  @Path("/djia")
	  @Produces("application/json")
    public MarketSummary getMarketSummary() {


       String url =  System.getenv("API_CONNECT_PROXY_URL") + "/stocks/stock-quote/djia";
       logger.info("Using Market summary  URL from env: " + url);


       WebTarget target = client.target(url);
       Response response = target.request().header("X-IBM-Client-Id", API_CONNECT_CLIENT_ID).get();


       String json = response.readEntity(String.class);
       logger.info("Got the following JSON for  Market summary: " + json);

       response.close();

       return jsonb.fromJson(json, MarketSummary.class);


    }

    @GET
	  @Path("/about")
	  @Produces("text/plain")
    public String getAbout() {


       String url =  System.getenv("API_CONNECT_PROXY_URL") + "/stocks/stock-quote/about";
       logger.info("Calling 'about' endpoint with: " + url);

       WebTarget target = client.target(url);
       Response response = target.request().header("X-IBM-Client-Id", API_CONNECT_CLIENT_ID).get();


       String text = response.readEntity(String.class);

       response.close();

       return text;

    }


   /* K8s readinesss endpoint */

    @GET
  	@Path("/readiness")
    @Produces("text/plain")
    public String  getReadiness() {
       return "ready";
    }


}
