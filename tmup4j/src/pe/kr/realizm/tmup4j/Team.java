package pe.kr.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class Team extends Tmup4J {
	
	@Override
	public JsonObject searchOrganization(int team, String query) throws IOException {
		return rest.request(RequestMethod.GET,
				ContentType.application_xwwwformurlencoded,
				AUTH_DOMAIN + "/v1/search/" + team,
				"query=" + query);
	}
}
