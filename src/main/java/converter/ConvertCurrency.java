package converter;

import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.math.BigDecimal;

import static exception.Exception.error;

public class ConvertCurrency{

    public static BigDecimal convertCurrency(RoutingContext routingContext, String from, String to, String amount) {
        String ACCESS_KEY = "x4JPlcZyJk1l2QYlI5cVgz8JIrr9wOJN";
        String BASE_URL = "https://api.apilayer.com/currency_data/";
        String ENDPOINT = "convert";

        // this object is used for executing requests to the (REST) API
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // the "from", "to" and "amount" can be set as variables
        HttpGet get = new HttpGet(BASE_URL + ENDPOINT + "?apikey=" + ACCESS_KEY + "&from=GBP&to=INR&amount=2");
        BigDecimal amountReturned = new BigDecimal(0);
        try {
            CloseableHttpResponse response =  httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
            amountReturned = jsonObject.getBigDecimal("result");

            response.close();
            httpClient.close();

        } catch (ClientProtocolException e) {
            error(routingContext, 500, "Error of protocol");
        } catch (IOException e) {
            error(routingContext, 500, "File system error");
        } catch (ParseException e) {
            error(routingContext, 500, "Parsing error");
        } catch (JSONException e) {
            error(routingContext, 500, "Json not received");
        }
        finally {
            try {
                httpClient.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return amountReturned;
    }
}
