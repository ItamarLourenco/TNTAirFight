package classes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {
 
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public final static int PUT = 3;
 
    public ServiceHandler() {
 
    }
    
    public class HeaderWs{
    	String type = null;
    	String value = null;
    }
 
    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
 
    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    
    public String makeServiceCall(String url, int method, String body, List<NameValuePair> params, String autenticacao) {
    	return makeServiceCall(url, method, body, params, autenticacao, true);
    }
    
        
    public String makeServiceCall(String url, int method, String body, List<NameValuePair> params, String autenticacao, boolean headerJson) {
    	if(body != null){
	    	try {
				body = new String(body.toString().getBytes("UTF-8"), "ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
    	}
    	
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
             
            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                
                if(headerJson == true){
                	httpPost.setHeader("content-type", "application/json");
                }
                                
                if(autenticacao != null){
                	httpPost.setHeader("X-AirFightApiKey", autenticacao);
                }
                
                
                if(body != null){
                	httpPost.setEntity(new StringEntity(body));
                }
                
                
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
 
                httpResponse = httpClient.execute(httpPost);
 
            } else if (method == GET) {
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
                
                httpGet.setHeader("content-type", "application/json");
                if(autenticacao != null){
                	httpGet.setHeader("X-AirFightApiKey", autenticacao);
                }
 
                httpResponse = httpClient.execute(httpGet);
 
            } else if(method == PUT){
            	HttpPut httpPut = new HttpPut(url);
            	httpPut.setHeader("content-type", "application/json");
            	if(body != null){
            		httpPut.setEntity(new StringEntity(body));
                }
            	httpResponse = httpClient.execute(httpPut);
            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
        return response;
 
    }
}