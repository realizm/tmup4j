package com.github.realizm.tmup4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Feed extends Tmup4J {

	Feed(Request request) {
		super(request);
	}

	@Override
	public JsonObject getFeedGroupList(int team_number) throws IOException {

		String param = null;
		if (team_number > -1) {
			param = "team=" + team_number;
		}

		return request.request(RequestMethod.GET, ContentType.application_xwwwformurlencoded,
				EDGE_DOMAIN + "/v3/feedgroups", param);
	}

	@Override
	public long getFeedGroupNumber(int team_number, String group_name) throws IOException {

		if (group_name == null) {
			throw new IllegalArgumentException("group_name is not allowed null.");
		}

		JsonObject result = getFeedGroupList(team_number);

		JsonArray feedGroups = result.getAsJsonArray("feedgroups");

		long feedGroupNumber = -1;

		for (JsonElement tFeedGroup : feedGroups) {
			JsonObject feedGroup = tFeedGroup.getAsJsonObject();
			if (group_name.equals(feedGroup.get("groupname").getAsString())) {
				feedGroupNumber = feedGroup.get("feedgroup").getAsLong();
				break;
			}
		}

		return feedGroupNumber;
	}

	@Override
	public long postFeed(long feedgroup_number, String content, boolean isMarkupContent, int team_number, File[] files,
			boolean force_alert) throws IOException {

		JsonObject param = new JsonObject();
		param.addProperty("content", content);
		param.addProperty("push", force_alert ? "1" : "0");

		if (files != null && files.length > 0) {

			JsonObject uploadResult = request.uploadFiles(team_number, files);
			JsonArray uploadResultArray = uploadResult.get("files").getAsJsonArray();

			JsonArray ids = new JsonArray();
			for (JsonElement je : uploadResultArray) {
				String uploadID = je.getAsJsonObject().get("id").getAsString();
				ids.add(uploadID);
			}

			param.add("ids", ids);
		}

		return postFeed(feedgroup_number, isMarkupContent, param);
	}
	
	@Override
	public long postFeed(long feedgroup_number, String content, boolean isMarkupContent, int team_number,
			InputStream[] input_streams, String[] file_names, boolean force_alert) throws IOException {

		JsonObject param = new JsonObject();
		param.addProperty("content", content);
		param.addProperty("push", force_alert ? "1" : "0");

		JsonObject uploadResult = request.uploadFiles(team_number, input_streams, file_names);
		JsonArray uploadResultArray = uploadResult.get("files").getAsJsonArray();
	
		JsonArray ids = new JsonArray();
		for (JsonElement je : uploadResultArray) {
			String uploadID = je.getAsJsonObject().get("id").getAsString();
			ids.add(uploadID);
		}
	
		param.add("ids", ids);

		return postFeed(feedgroup_number, isMarkupContent, param);
	}
	
	@Override
	public long postFeed(long feedgroup_number, boolean isMarkupContent, JsonObject param) throws IOException {
		return request.request(RequestMethod.POST,
				ContentType.appliaction_json,
				EDGE_DOMAIN + "/v3/feed/" + feedgroup_number + "/" + (isMarkupContent ? "2" : "1"),
				param.toString()).get("feed").getAsLong();
	}
}
