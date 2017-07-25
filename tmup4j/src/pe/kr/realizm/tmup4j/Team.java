package pe.kr.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class Team extends Tmup4J {
	
	Team(Request request) {
		super(request);
	}

	@Override
	public JsonObject searchOrganization(int team, String query) throws IOException {
		return request.request(RequestMethod.GET,
				ContentType.application_xwwwformurlencoded,
				AUTH_DOMAIN + "/v1/search/" + team,
				"query=" + query);
	}
}
