package com.github.realizm.tmup4j;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class JsonHelper {

	static JsonObject toJson(String jsonString) {

		JsonParser jp = new JsonParser();
		return (JsonObject) jp.parse(jsonString);
	}

}
