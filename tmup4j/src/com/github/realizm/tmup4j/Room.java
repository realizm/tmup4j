package com.github.realizm.tmup4j;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class Room extends Tmup4J {
	
	Room(Request request) {
		super(request);
	}

	@Override
	public JsonObject getRoomList(int team_number) throws IOException {
		
		return request.request(RequestMethod.GET,
				ContentType.application_xwwwformurlencoded,
				EDGE_DOMAIN + "/v3/rooms" + (team_number > -1 ? ("/" + team_number) : ""),
				null);
	}
	
	@Override
	public long createRoom(int team_number, int[] user_numbers) throws IOException {
		
		JsonObject param = new JsonObject();
		JsonArray users = new JsonArray();
		for(int userNumber:user_numbers) {
			users.add(userNumber);
		}
		
		param.add("users", users);
		
		return request.request(RequestMethod.POST,
				ContentType.appliaction_json,
				EDGE_DOMAIN + "/v3/room/" + team_number,
				param.toString()).get("room").getAsLong();
	}
	
	
	private int MY_USER_NUMBER = -1;
	
	private HashMap<Integer, HashMap<Integer, Long>> STORED_ROOM_NUMBER 
			= new HashMap<Integer, HashMap<Integer, Long>>();
	
	@Override
	public long getRoomNumber(int team_number, int user_number) throws IOException {
		
		if(MY_USER_NUMBER == -1){
			MY_USER_NUMBER = new My(request).getMyInfo().get("index").getAsInt();
		}
		
		if(MY_USER_NUMBER == user_number){
			throw new IOException("Do NOT create message room with myself.");
		}
		
		HashMap<Integer, Long> storedRoomNumberMap;
		if(STORED_ROOM_NUMBER.containsKey(team_number)) {
			storedRoomNumberMap = STORED_ROOM_NUMBER.get(team_number);
			if(storedRoomNumberMap.containsKey(user_number)) {
				return storedRoomNumberMap.get(user_number);
			}
		} else {
			storedRoomNumberMap = new HashMap<Integer, Long>();
			STORED_ROOM_NUMBER.put(team_number, storedRoomNumberMap);
		}
		
		//teamup에서 1:1대화방이 있을경우 createRoom을 하더라도 기존방번호를 반환한다.
		int[] user_numbers = {user_number};
		long roomNumber = createRoom(team_number, user_numbers);
		storedRoomNumberMap.put(user_number, roomNumber);
		
		return roomNumber;
	}
	
}
