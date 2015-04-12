package com.nikitaend.instafeed.sola.instagram.auth;

import java.io.Serializable;

public class AccessToken implements Serializable{
	String token;
	
	public AccessToken(String token) {
		this.setTokenString(token);
	}
	
	public String getTokenString() {
		return token;
	}

	public void setTokenString(String token) {
		this.token = token;
	}

	public String toString() {
		return getTokenString();
	}
}
