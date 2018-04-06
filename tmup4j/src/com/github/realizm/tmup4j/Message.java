package com.github.realizm.tmup4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonObject;

class Message extends Tmup4J {

	Message(Request request) {
		super(request);
	}

	@Override
	public long sendMessage(int team_number, int user_number, String content) throws IOException {
		
		long roomNumber = new Room(request).getRoomNumber(team_number, user_number);
		JsonObject param = new JsonObject();
		param.addProperty("content", content);
		
		return sendMessage(roomNumber, 1, param);
	}
	
	@Override
	public long sendMessage(int team_number, int user_number, File file) throws IOException {
		
		long roomNumber = new Room(request).getRoomNumber(team_number, user_number);
		
		JsonObject uploadResult = request.uploadFile(team_number, file);
		String fileId = uploadResult.get("files").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
		
		JsonObject param = new JsonObject();
		param.addProperty("content", fileId);
		
		return sendMessage(roomNumber, 2, param);
	}
	
	@Override
	public long sendMessage(int team_number, int user_number, InputStream input_stream, String file_name) throws IOException {
		
		long roomNumber = new Room(request).getRoomNumber(team_number, user_number);
		
		JsonObject uploadResult = request.uploadFiles(team_number, new InputStream[] {input_stream}, new String[] {file_name});
		String fileId = uploadResult.get("files").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
		
		JsonObject param = new JsonObject();
		param.addProperty("content", fileId);
		
		return sendMessage(roomNumber, 2, param);
	}
	
	@Override
	public long sendMessage(long room_number, JsonObject param) throws IOException {
		return sendMessage(room_number, 1, param);
	}
	
	@Override
	public long sendMessage(long room_number, int type, JsonObject param) throws IOException {
		
		JsonObject result = request.request(RequestMethod.POST,
				ContentType.appliaction_json, 
				EDGE_DOMAIN + "/v3/message/" + room_number + "/" + type,
				param.toString());
		
		return result.get("msg").getAsLong();
	}
}
