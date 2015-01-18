package com.nedzhang.sterlingUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;

import com.nedzhang.util.CharsetUtil;

public class SterlingHttpTesterUtil {

	public static String invoke(String httpApiInteropUrl, 
			String userID, String password, String accessToken,
			String progID, String apiName, boolean isFlow, String input, String outputTemplate) throws ClientProtocolException, IOException {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(httpApiInteropUrl);
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        
        
        nvps.add(new BasicNameValuePair("YFSEnvironment.progId", progID));
		nvps.add(new BasicNameValuePair("InteropApiName", apiName));
		if (isFlow) {
			nvps.add(new BasicNameValuePair("ServiceName", apiName));
			nvps.add(new BasicNameValuePair("IsFlow", "Y"));
		} else {
			nvps.add(new BasicNameValuePair("ApiName", apiName));
			nvps.add(new BasicNameValuePair("IsFlow", "N"));
		}

		nvps.add(new BasicNameValuePair("YFSEnvironment.userId", userID));
		if (accessToken != null && accessToken.length() > 0) {
			nvps.add(new BasicNameValuePair("YFSEnvironment.userToken", accessToken));
		} else if (password != null && password.length() > 0) {
			nvps.add(new BasicNameValuePair("YFSEnvironment.password", password));
		}
		
		nvps.add(new BasicNameValuePair("YFSEnvironment.version", ""));
		nvps.add(new BasicNameValuePair("YFSEnvironment.locale", ""));
		nvps.add(new BasicNameValuePair("InteropApiData", input));
		nvps.add(new BasicNameValuePair("TemplateData", outputTemplate));
		
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse response2 = httpclient.execute(httpPost);

        try {
//            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream responseStream =  entity2.getContent();
            
            String responseText = CharsetUtil.decodeStream(responseStream, "UTF-8");
            
            EntityUtils.consume(entity2);
            
            return responseText;
        } finally {
            httpPost.releaseConnection();
        }
        

	}

}
