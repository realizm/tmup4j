package com.github.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class My extends Tmup4J {
	
	My(Request request) {
		super(request);
	}

	@Override
	public JsonObject getMyInfo() throws IOException {
		return request.request(RequestMethod.GET,
				ContentType.application_xwwwformurlencoded,
				AUTH_DOMAIN + "/v1/user",
				null);
		
	}
	
	
}
