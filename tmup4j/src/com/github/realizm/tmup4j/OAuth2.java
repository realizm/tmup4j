package com.github.realizm.tmup4j;

import java.io.IOException;
import java.util.Calendar;

import com.google.gson.JsonObject;

class OAuth2 {
	
	private enum GrantType {
		authorization_code,
		password,		
	}
	
	private Request request = null;
	private String AUTH_DOMAIN = null;
	
	private String CLIENT_ID = null;
	private String CLIENT_SECRET = null;
	
	private GrantType GRANT_TYPE = null;
	private String AUTHORIZATION_CODE = null;
	private String USERNAME = null;
	private String PASSWORD = null;
	
	OAuth2(Request request, String authDomain, String client_id, String client_secret) {
		this.request = request;
		this.AUTH_DOMAIN = authDomain;
		this.CLIENT_ID = client_id;
		this.CLIENT_SECRET = client_secret;
	}
	
	void setAuthDomain(String authDomain) {
		this.AUTH_DOMAIN = authDomain;
	}
	
	JsonObject oAuth2(String username, String password) throws IllegalAccessException, IOException {
		
		GRANT_TYPE = GrantType.password;
		USERNAME = username;
		PASSWORD = password;

		String param = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
				CLIENT_ID, CLIENT_SECRET, username, password);
		return oAuth2Impl(param);
	}

	JsonObject oAuth2(String authorizationCode) throws IllegalAccessException, IOException {
		
		GRANT_TYPE = GrantType.authorization_code;
		AUTHORIZATION_CODE = authorizationCode;

		String param = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s", CLIENT_ID,
				CLIENT_SECRET, authorizationCode);
		return oAuth2Impl(param);
	}

	private JsonObject oAuth2Impl(String param) throws IllegalAccessException, IOException {
		
		Calendar cal = Calendar.getInstance();
		
		request.clearToken();
		
		JsonObject result;
		try {
			result = request.request(RequestMethod.POST, ContentType.application_xwwwformurlencoded,
					AUTH_DOMAIN + "/oauth2/token", param);
		} catch (IOException e) {
			throw e;
		}

		if (result.keySet().contains("exception")) {
			String error = result.get("error").getAsString();
			String desc = result.get("error_description").getAsString();
			throw new IllegalAccessException(String.format("Error : %s - %s", error, desc));
		} else {
			String tokenType = result.get("token_type").getAsString();
			String accessToken = result.get("access_token").getAsString();
			String refreshToken = result.get("refresh_token").getAsString();
			int expiresIn = result.get("expires_in").getAsInt();
			
			cal.add(Calendar.SECOND, expiresIn - 60);
			
			request.setToken(tokenType, accessToken, refreshToken, cal.getTimeInMillis());
		}

		return result;
	}

	JsonObject refreshToken() throws IOException {
		String param = String.format("grant_type=refresh_token&refresh_token=%s",
				request.getRefreshToken());
		
		try {
			return oAuth2Impl(param);
		} catch (IllegalAccessException e) {
			return redoOAuth2();
		}
	}
	
	private JsonObject redoOAuth2() throws IOException {
		try {
			if(GRANT_TYPE == GrantType.authorization_code) {
				return oAuth2(AUTHORIZATION_CODE);
			} else if(GRANT_TYPE == GrantType.password) {
				return oAuth2(USERNAME, PASSWORD);
			} else {
				throw new IOException("Not logged in.");
			}
		} catch (IllegalAccessException e) {
			throw new IOException(e);
		}
	}
}
