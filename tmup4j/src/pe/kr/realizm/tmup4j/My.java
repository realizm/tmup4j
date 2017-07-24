package pe.kr.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class My extends Tmup4J {
	
	@Override
	public JsonObject getMyInfo() throws IOException {
		return rest.request(RequestMethod.GET,
				ContentType.application_xwwwformurlencoded,
				AUTH_DOMAIN + "/v1/user",
				null);
		
	}
	
	
}
