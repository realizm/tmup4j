package pe.kr.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class OAuth2 {
	
	private Request request = null;
	private String AUTH_DOMAIN = null;
	
	private String CLIENT_ID = null;
	private String CLIENT_SECRET = null;
	

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

		String param = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
				CLIENT_ID, CLIENT_SECRET, username, password);
		return oAuth2Impl(param);
	}

	JsonObject oAuth2(String code) throws IllegalAccessException, IOException {

		String param = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s", CLIENT_ID,
				CLIENT_SECRET, code);
		return oAuth2Impl(param);
	}

	private JsonObject oAuth2Impl(String param) throws IllegalAccessException, IOException {
		
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

			request.setToken(tokenType, accessToken, refreshToken);
		}

		return result;
	}


}
