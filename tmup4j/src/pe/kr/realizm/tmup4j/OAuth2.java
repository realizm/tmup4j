package pe.kr.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class OAuth2 extends Tmup4J {

	public JsonObject oAuth2(String username, String password) throws IllegalAccessException, IOException {

		String param = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
				CLIENT_ID, CLIENT_SECRET, username, password);
		return oAuth2Impl(param);
	}

	public JsonObject oAuth2(String code) throws IllegalAccessException, IOException {

		String param = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s", CLIENT_ID,
				CLIENT_SECRET, code);
		return oAuth2Impl(param);
	}

	private JsonObject oAuth2Impl(String param) throws IllegalAccessException, IOException {

		JsonObject result;
		try {
			result = rest.request(RequestMethod.POST, ContentType.application_xwwwformurlencoded,
					AUTH_DOMAIN + "/oauth2/token", param);
		} catch (IOException e) {
			clearAuthorization();
			throw e;
		}

		if (result.keySet().contains("exception")) {
			clearAuthorization();
			String error = result.get("error").getAsString();
			String desc = result.get("error_description").getAsString();
			throw new IllegalAccessException(String.format("Error : %s - %s", error, desc));
		} else {
			Tmup4J.TOKEN_TYPE = result.get("token_type").getAsString();
			Tmup4J.ACCESS_TOKEN = result.get("access_token").getAsString();
			Tmup4J.REFRESH_TOKEN = result.get("refresh_token").getAsString();

			rest.setAuthorization(TOKEN_TYPE + " " + ACCESS_TOKEN);
		}

		return result;
	}

	private void clearAuthorization() {

		Tmup4J.TOKEN_TYPE = null;
		Tmup4J.ACCESS_TOKEN = null;
		Tmup4J.REFRESH_TOKEN = null;

		rest.setAuthorization(null);
	}

}
