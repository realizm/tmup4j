package pe.kr.realizm.tmup4j;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonObject;

public class Tmup4J {
	protected static String AUTH_DOMAIN = "https://auth.tmup.com";
	protected static String EDGE_DOMAIN = "https://edge.tmup.com";
	protected static String FILE_DOMAIN = "https://file.tmup.com";

	protected static String CLIENT_ID = null;
	protected static String CLIENT_SECRET = null;

	protected static String TOKEN_TYPE = null;
	protected static String ACCESS_TOKEN = null;
	protected static String REFRESH_TOKEN = null;

	protected RestHelper rest = new RestHelper();

	protected Tmup4J() {
	}

	/**
	 * Tmup4J (https://github.com/realizm/tmup4j)
	 * 
	 * @version : 0.1.0 alpha
	 * @author : Kim taejung (realizmk at gmail.com)
	 * @param client_id
	 * @param client_secret
	 */
	public Tmup4J(String client_id, String client_secret) {
		Tmup4J.CLIENT_ID = client_id;
		Tmup4J.CLIENT_SECRET = client_secret;
	}

	/**
	 * <h1>oauth2 - Password Credentials</h1>
	 * 
	 * @see also http://team-up.github.io/oauth2/#api-oauth2-getOauth2TokenPassword
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	public JsonObject oAuth2(String username, String password) throws IllegalAccessException, IOException {
		return new OAuth2().oAuth2(username, password);
	}

	/**
	 * <h1>oauth2 - Authorization Code #2 (NOT TESTED)</h1>
	 * 발급 받은 code 로 token 발급
	 * 
	 * @see also http://team-up.github.io/oauth2/#api-oauth2-postOauth2TokenCode2
	 * 
	 * @param code
	 * @return
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	public JsonObject oAuth2(String code) throws IllegalAccessException, IOException {
		return new OAuth2().oAuth2(code);
	}
	
	/**
	 * <h1>my - 내 정보</h1>
	 * 
	 * @see also http://team-up.github.io/v1/auth/#api-my-getUser
	 * 
	 * @return My Info 
	 * @throws IOException
	 */
	public JsonObject getMyInfo() throws IOException {
		return new My().getMyInfo();
	}
	
	/**
	 * <h1>team - 조직도 검색</h1>
	 * 이름(초성가능), 부서, 이메일 검색
	 * 
	 * @see also http://team-up.github.io/v1/auth/#api-team-getSearch
	 * 
	 * @param team_number
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public JsonObject searchOrganization(int team_number, String query) throws IOException {
		return new Team().searchOrganization(team_number, query);
	}

	/**
	 * <h1>group - 그룹 목록</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/feed/#api-group-getFeedGroups
	 * 
	 * @return
	 * @throws IOException
	 */
	public JsonObject getFeedGroupList() throws IOException {
		return new Feed().getFeedGroupList(-1);
	}

	/**
	 * <h1>group - 그룹 목록</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/feed/#api-group-getFeedGroups
	 * 
	 * @param team_number
	 * @return
	 * @throws IOException 
	 */
	public JsonObject getFeedGroupList(int team_number) throws IOException {
		return new Feed().getFeedGroupList(team_number);
	}

	/**
	 * <h1>피드이름으로 피드번호를 가져온다</h1>
	 * 
	 * @param team_number
	 * @param feed_name
	 * @return
	 * @throws IOException
	 */
	public long getFeedGroupNumber(int team_number, String feed_name) throws IOException {
		return new Feed().getFeedGroupNumber(team_number, feed_name);
	}

	/**
	 * <h1>feed - 피드 생성</h1>
	 * 
	 * @param feedgroup_number
	 * @param content
	 * @return feed_number
	 * @param force_alert
	 * @throws IOException 
	 */
	public long postFeed(long feedgroup_number, String content, boolean force_alert) throws IOException {
		return new Feed().postFeed(feedgroup_number, content, -1, new File[0], force_alert);
	}

	/**
	 * <h1>feed - 피드 생성 (파일 첨부)</h1>
	 * 
	 * @param feedgroup_number
	 * @param content
	 * @param team_number
	 * @param attach_file
	 * @param force_alert
	 * @return feed_number
	 * @throws IOException
	 */
	public long postFeed(long feedgroup_number, String content, int team_number, File attach_file, boolean force_alert) throws IOException {
		File[] attach_files = { attach_file };
		return new Feed().postFeed(feedgroup_number, content, team_number, attach_files, force_alert);
	}

	/**
	 * <h1>feed - 피드 생성 (다중 파일 첨부)</h1>
	 * 
	 * @param feedgroup_number
	 * @param content
	 * @param team_number
	 * @param attach_files
	 * @param force_alert
	 * @return feed_number
	 * @throws IOException
	 */
	public long postFeed(long feedgroup_number, String content, int team_number, File[] attach_files, boolean force_alert) throws IOException {
		return new Feed().postFeed(feedgroup_number, content, team_number, attach_files, force_alert);
	}
	
	/**
	 * <h1>feed - 피드 생성</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/feed/#api-feed-postFeed
	 * 
	 * @param feedgroup_number
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public long postFeed(long feedgroup_number, JsonObject param) throws IOException {
		return new Feed().postFeed(feedgroup_number, param);
	}
	
	/**
	 * <h1>room - 대화방 목록</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/chat/#api-room-getRooms
	 * 
	 * @return
	 * @throws IOException
	 */
	public JsonObject getRoomList() throws IOException {
		return new Room().getRoomList(-1);
	}
	
	/**
	 * <h1>room - 대화방 목록</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/chat/#api-room-getRooms
	 * 
	 * @param team_number
	 * @return
	 * @throws IOException
	 */
	public JsonObject getRoomList(int team_number) throws IOException {
		return new Room().getRoomList(team_number);
	}
	
	/**
	 * <h1> room - 대화방생성</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/chat/#api-room-postRoom
	 * 
	 * @param team_number
	 * @param user_numbers
	 * @return
	 * @throws IOException
	 */
	public long createRoom(int team_number, int[] user_numbers) throws IOException {
		return new Room().createRoom(team_number, user_numbers);
	}
	
	/**
	 * <h1>message - 메시지 생성</h1>
	 * 
	 * @param team_number
	 * @param user_number
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public long sendMessage(int team_number, int user_number, String content) throws IOException {
		return new Message().sendMessage(team_number, user_number, content);
	}
	
	/**
	 * <h1>message - 메시지 생성</h1>
	 * 
	 * @see also http://team-up.github.io/v3/edge/chat/#api-message-postMessage
	 * 
	 * @param room_number
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public long sendMessage(long room_number, JsonObject param) throws IOException {
		return new Message().sendMessage(room_number, param);
	}
		
	public void setAuthDomain(String auth_domain) {
		Tmup4J.AUTH_DOMAIN = auth_domain;
	}
	
	public void setEdgeDomain(String edge_domain) {
		Tmup4J.EDGE_DOMAIN = edge_domain;
	}
	
	public void setFileDomain(String file_domain) {
		Tmup4J.FILE_DOMAIN = file_domain;
	}

}
