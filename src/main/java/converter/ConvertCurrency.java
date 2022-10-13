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

public class ConvertCurrency{

    public static void convertCurrency(RoutingContext routingContext, String from, String to, String amount) {
        String ACCESS_KEY = "x4JPlcZyJk1l2QYlI5cVgz8JIrr9wOJN";
        String BASE_URL = "https://api.apilayer.com/currency_data/";
        String ENDPOINT = "convert";

        // this object is used for executing requests to the (REST) API
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // the "from", "to" and "amount" can be set as variables
        HttpGet get = new HttpGet(BASE_URL + ENDPOINT + "?apikey=" + ACCESS_KEY + "&from=GBP&to=INR&amount=2");
        try {
            CloseableHttpResponse response =  httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));

            System.out.println("Single-Currency Conversion");

            // parsed JSON Objects are accessed according to the JSON resonse's hierarchy, output strings are built
            System.out.println("From : " + jsonObject.getJSONObject("query").getString("from"));
            System.out.println("To : " + jsonObject.getJSONObject("query").getString("to"));
            System.out.println("Amount : " + jsonObject.getJSONObject("query").getLong("amount"));
            System.out.println("Conversion Result : " + jsonObject.getDouble("result"));
            System.out.println("\n");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            try {
                httpClient.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }

//        WebClient client = WebClient.create(routingContext.vertx());
//
//        client.get(8080, "https://api.apilayer.com/currency_data/convert?to="+to+"&from="+from+"&amount="+amount+"&apikey=TfPHNX2mleY2GVdObHIhov8l38kTQRXD", "/")
//                .as(BodyCodec.jsonObject())
//                .send(ar -> {
//                    if (ar.succeeded()) {
//                        HttpResponse<JsonObject> response = ar.result();
//                        System.out.println("Got HTTP response body");
//                        System.out.println(response.body().encodePrettily());
//                    } else {
//                        ar.cause().printStackTrace();
//                        System.out.println("Error");
//                    }
//                });





//        HttpClientOptions options = new HttpClientOptions();
//
//        options.setLogActivity(true);
//        options.setDefaultPort(8080);
//
//        HttpClient client = routingContext.vertx().createHttpClient(options);
//
//        client.request(HttpMethod.GET,"api.apilayer.com/currency_data/convert?to="+to+"&from="+from+"&amount="+amount+"&apikey=TfPHNX2mleY2GVdObHIhov8l38kTQRXD", response -> {
//            System.out.println("Received response with status code " + response.statusCode());
//            int code = response.statusCode();
//            if (code == 200) {
//                response.bodyHandler(bufferResponse -> {
//                    // Adapt according your response type, could be String as well
//                    JsonObject httpResult = bufferResponse.toJsonObject();
//                    System.out.println("Received HTTP response with body " + httpResult);
//                    asyncResponse.resume(Response.ok(httpResult).build());
//                });
//            } else {
//
//                response.bodyHandler(bufferResponse -> {
//                    // Return null in a JSON Object in case of error
//                    String httpResult = "{null}";
//                    asyncResponse.resume(Response.status(code).entity(httpResult).build());
//                });
//            }
//
//        }).putHeader(HttpHeaders.CONTENT_TYPE, "").end();

//            WebClient.create(vertx);

//        client.get(8080, "www.google.fr", "/");
//                client.send(ar -> {
//                    if (ar.succeeded()) {
//                        HttpResponse<Buffer> response = ar.result();
//                        if (response.statusCode() == 200 && response.getHeader("content-type").equals("application/json")) {
//                            // Decode the body as a json object
//                            JsonObject body = response.bodyAsJsonObject();
//                        } else {
//                            System.out.println("Something went wrong " + response.statusCode());
//                        }
//                    } else {
//                        System.out.println("Something went wrong " + ar.cause().getMessage());
//                    }
//                });

        //        HttpRequest<JsonObject> request =

//                .get(443, "https://api.apilayer.com/currency_data/convert?to="+to+"&from="+from+"&amount="+amount, "/")
//                .ssl(true)
//                .putHeader("Accept", "application/json")
//                .putHeader("apikey", "TfPHNX2mleY2GVdObHIhov8l38kTQRXD")
//                .as(BodyCodec.string()).send(ar -> {
//                    if(ar.succeeded()) {
//                        HttpResponse<String> response = ar.result();
//                        System.out.println("Got HTTP response body");
//                        System.out.println(response.body());
//                    }
//                    else {
//                        ar.cause().printStackTrace();
//                    }
//                });
//                .expect(ResponsePredicate.SC_OK);
//
//        client.get("/data/2.5/weather?q=london,uk&units=metric&appid=e38f373567e83d2ba1b6928384435689").as(BodyCodec.string()).send(ar -> {
//            if(ar.succeeded()) {
//                HttpResponse<String> response = ar.result();
//                System.out.println("Got HTTP response body");
//                System.out.println(response.body());
//            }
//            else {
//                ar.cause().printStackTrace();
//            }
//        });
//
//        request.send(asyncResult -> {
//            if (asyncResult.succeeded()) {
//                System.out.println(asyncResult.result().body()); // (7)
//            }
//        });
//        return BigDecimal.valueOf(0);
    }
}
