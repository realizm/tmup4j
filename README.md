Tmup4J �̽�Ʈ����Ʈ�� Team-up API�� �����ϴ� ���̺귯���Դϴ�. 

Tmup4J is a EstSoft Team-up API binding library for the Java language licensed under Apache License 2.0.

Tmup4J includes software from gson.google.com to parse JSON request/response from the Team-up API. You can see the license term at https://github.com/google/gson/blob/master/LICENSE

======= Example =====

package test.pe.kr.realizm.tmup4j;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pe.kr.realizm.tmup4j.Tmup4J;

public class TestTmup4J {

	public static void main(String[] args) throws Exception {
		
		
		Tmup4J tmup4J = new Tmup4J("CLIENT_ID", "CLIENT_SECRET");
		tmup4J.oAuth2("USER@EMAIL","PASSWORD");
		
		
		//�������� ���ؿ´�
		JsonObject myInfo = tmup4J.getMyInfo();
		
		//����ȣ ȹ��
		JsonArray teamInfos = myInfo.get("teams").getAsJsonArray();
		int teamIdx = teamInfos.get(0).getAsJsonObject().get("index").getAsInt();
		
		
		/*feed�߼�*/
		
		//feed�˻�
		long feedGroupNumber = tmup4J.getFeedGroupNumber(teamIdx, "�׽�Ʈ�ǵ�");
		
		//feed�߼�
		String feedContent = "�ǵ� ������";
		File[] attachFiles = {new File("AnyOne"), new File("AnotherOne")};
		tmup4J.postFeed(feedGroupNumber, feedContent, teamIdx, attachFiles, false);
		
		
		//chat
		//user�˻�
		JsonObject searchResult = tmup4J.searchOrganization(teamIdx, "RECIEVER@EMAIL");
		JsonArray users = searchResult.get("users").getAsJsonArray();
		int userIdx = users.get(0).getAsJsonObject().get("index").getAsInt();
		
		//chat�߼�
		tmup4J.sendMessage(teamIdx, userIdx, "�޽���");
		
		
	}

}
