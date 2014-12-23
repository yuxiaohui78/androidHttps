package com.example.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.example.ssl.util.TwoWaysAuthenticationSSLSocketFactory;

public class MainActivity extends Activity implements View.OnClickListener{
	private static final String HTTPS_URL = "https://192.168.159.129:8443/";
	private WebView webView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		findViewById (R.id.https_httpclient).setOnClickListener(this);
		findViewById (R.id.https_url_connection).setOnClickListener(this);
		webView = (WebView)findViewById (R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.https_httpclient){
			runHttpsRequestWithHttpClient ();
			return;
		}
		
		if (v.getId() == R.id.https_url_connection){
			runHttpsRequestWithHttpsURLConnection ();
			return;
		}
	}
	
	private void runHttpsRequestWithHttpsURLConnection(){
		AsyncTask <String, Void, String> testTask = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... params) {
				String result = "";
				HttpsURLConnection conn = null;
				try {
					URL url = new URL(HTTPS_URL); 
					conn = (HttpsURLConnection) url.openConnection();
					conn.setSSLSocketFactory(TwoWaysAuthenticationSSLSocketFactory.getSSLSocketFactory(MainActivity.this));
					conn.connect();
					result = parseSendMessageResponse(conn.getInputStream());
					Log.e("HttpsURLConnection Response=====>", result);
					return result;
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (NoSuchAlgorithmException e){
					e.printStackTrace();
				}catch (KeyManagementException e){
					e.printStackTrace();
				}catch (Exception e){
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				showHttpPage (result);
			}
		};

		testTask.execute();
	}
	
	private  static String parseSendMessageResponse(InputStream in) throws Exception
	{
		// Read from the input stream and convert into a String.
		InputStreamReader inputStream = new InputStreamReader(in);
		BufferedReader buff = new BufferedReader(inputStream);

		StringBuilder sb = new StringBuilder();
		String line = buff.readLine();
		while(line != null)
		{          
			sb.append(line);
			line = buff.readLine();
		}

		return sb.toString();
	}
	
	private void runHttpsRequestWithHttpClient(){
		AsyncTask <String, Void, String> testTask = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... params) {
				try {
					HttpClient httpsClient = AppSslApplication.getHttpsClient(MainActivity.this.getBaseContext());
					HttpGet httpget = new HttpGet(HTTPS_URL);
					HttpResponse response = httpsClient.execute(httpget);
					HttpEntity entity = response.getEntity();
					Log.e("Response status", response.getStatusLine().toString());
					String result = "";
					if (entity != null) {
						Log.e("Response", "Response content length: " + entity.getContentLength());
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
						String text;
						while ((text = bufferedReader.readLine()) != null) {
							result += text;
						}
						bufferedReader.close();
					}
					
					Log.e("HttpClient Response=====>", result);
					
					return result;
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				showHttpPage (result);
			}
		};

		testTask.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void showHttpPage (String data){
		webView.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
	}

}
