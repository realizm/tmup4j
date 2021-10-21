# Tmup4J 
이스트소프트의 Team-up API에 대응하는 JAVA 라이브러리입니다. 


## Goal
Team-up에서 Feed올리기 및 Chat발송을 편하게 하기 위해 만들어졌습니다. 


## Version history
* v0.2.3
	+ connect timeout수정 (default 2000msec)
	+ read timeout수정 (default 5000msec)
	+ setConnectTimeout (int ms) 메소드 추가
	+ setReadTimeout (int ms) 메소드 추가
	+ Build Encoding 'utf-8'로 변경
* v0.2.2
	+ 파일전송 기능 추가
	+ 내부 메소드로 사용하던 getRoomNumber 메소드 외부로 노출
	+ 자기자신에게 메시지 발송 시도 시 엉뚱한 사람에게 메시지가 가는 버그 수정
	+ Maven 프로젝트로 변경
* v0.2.0
	+ com.github.realizm.tmup4j로 패키지명 변경
	+ 토큰 리프레시 및 리프레시 토큰 만료시 재 로그인 기능 추가
	+ 한글깨짐 현상 추가수정
	+ 방번호 찾는 로직에 캐싱 로직 추가
* v0.1.3a
	+ REST API호출시 캐릭터셋 UTF-8 고정
* v0.1.2a
	+ domain관련 변수에 static 재생성
	+ Auth상태인지를 반환하는 isAuth함수 추가, License 생성
* v0.1.1a 
	+ 변수들에 static제거 및 생성자 수정
	+ postFeed함수에 markup_content파리미터 추가
* v0.1.0a 
	+ 초기 릴리즈

## Library dependency
- [com.google.gson](https://github.com/google/gson) Json Parameter 생성 및 Response 파싱. 


## License
Tmup4J는 [Apache 2.0 License](https://github.com/realizm/tmup4j/blob/master/LICENSE)에 따라 사용하실 수 있습니다.


## Sample code

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
