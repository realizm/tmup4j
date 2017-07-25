package pe.kr.realizm.tmup4j;

enum ContentType {

	application_xwwwformurlencoded("application/x-www-form-urlencoded"),
	appliaction_json("application/json"),
	multipart_formdata("multipart/form-data");

	private final String value;

	ContentType(String value) {
		this.value = value;
	}

	String getType() {
		return value;
	}

}
