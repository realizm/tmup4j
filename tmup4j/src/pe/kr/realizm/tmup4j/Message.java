package pe.kr.realizm.tmup4j;

import java.io.IOException;

import com.google.gson.JsonObject;

class Message extends Tmup4J {

	@Override
	public long sendMessage(int team_number, int user_number, String content) throws IOException {
		
		long roomNumber = new Room().getRoomNumber(team_number, user_number);
		JsonObject param = new JsonObject();
		param.addProperty("content", content);
		
		return sendMessage(roomNumber, param);
		
	}
	
	@Override
	public long sendMessage(long room_number, JsonObject param) throws IOException {
		
		JsonObject result = rest.request(RequestMethod.POST,
				ContentType.appliaction_json, 
				EDGE_DOMAIN + "/v3/message/" + room_number,
				param.toString());
		
		return result.get("msg").getAsLong();
	}
}
