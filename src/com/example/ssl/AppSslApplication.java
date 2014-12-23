package com.example.ssl;

import org.apache.http.client.HttpClient;

import android.app.Application;
import android.content.Context;

import com.example.ssl.util.HttpClientSslHelper;

public class AppSslApplication extends Application {

	private static AppSslApplication mAppSslInstance;

	private static HttpClient mHttpsClient;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppSslInstance = this;

	}

	public static AppSslApplication getInstance() {
		if(mAppSslInstance==null){
			mAppSslInstance = new AppSslApplication();
		}
		return mAppSslInstance;
	}

	public static HttpClient getHttpsClient(Context mContext) {
		if (mHttpsClient == null) {
			mHttpsClient = HttpClientSslHelper.getSslHttpClient(mContext);
		}
		return mHttpsClient;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
