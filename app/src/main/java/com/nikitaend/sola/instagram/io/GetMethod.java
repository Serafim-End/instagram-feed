package com.nikitaend.instafeed.sola.instagram.io;

import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;


public class GetMethod extends APIMethod {
	
	
	public GetMethod() {
		super();
		this.type = "GET";
	}
	
	public GetMethod(String methodUri) {
		super(methodUri);
	}	

	@Override
	protected InputStream performRequest() {
		HttpResponse response;
        InputStream stream = null;
		HttpGet post = new HttpGet(this.methodUri);
		try {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

			response = client.execute(post);
			stream = response.getEntity().getContent();

		} catch (Exception e) {
			e.printStackTrace();
		}  
		return stream;
	}
}
