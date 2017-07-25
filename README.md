Tmup4J 이스트소프트의 Team-up API에 대응하는 라이브러리입니다. 

Tmup4J is a EstSoft Team-up API binding library for the Java language licensed under Apache License 2.0.

Tmup4J includes software from gson.google.com to parse JSON request/response from the Team-up API. You can see the license term at https://github.com/google/gson/blob/master/LICENSE




	public static void main(String[] args) throws Exception {
		
		
		Tmup4J tmup4J = new Tmup4J("CLIENT_ID", "CLIENT_SECRET");
		tmup4J.oAuth2("USER@EMAIL","PASSWORD");
		
		
		//내정보를 구해온다
		JsonObject myInfo = tmup4J.getMyInfo();
		
		//팀번호 획득
		JsonArray teamInfos = myInfo.get("teams").getAsJsonArray();
		int teamIdx = teamInfos.get(0).getAsJsonObject().get("index").getAsInt();
		
		
		/*feed발송*/
		
		//feed검색
		long feedGroupNumber = tmup4J.getFeedGroupNumber(teamIdx, "테스트피드");
		
		//feed발송
		String feedContent = "<h1>피드 컨텐츠</h1>";
		File[] attachFiles = {new File("AnyOne"), new File("AnotherOne")};
		tmup4J.postFeed(feedGroupNumber, feedContent, true, teamIdx, attachFiles, false);
		
		
		//chat
		//user검색
		JsonObject searchResult = tmup4J.searchOrganization(teamIdx, "RECIEVER@EMAIL");
		JsonArray users = searchResult.get("users").getAsJsonArray();
		int userIdx = users.get(0).getAsJsonObject().get("index").getAsInt();
		
		//chat발송
		tmup4J.sendMessage(teamIdx, userIdx, "메시지");
		
		
	}
