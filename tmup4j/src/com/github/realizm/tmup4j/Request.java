package com.github.realizm.tmup4j;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

class Request {
	
	private String ACCESS_TOKEN = null;
	private String REFRESH_TOKEN = null;
	private long ON_EXPIRATION = 0;
	
	private int connectTimeout = 2000;
	private int readTimeout = 5000;
	
	void setConnectTimeout(int ms){
		this.connectTimeout = ms;
	}
	
	void setReadTimeout(int ms){
		this.readTimeout = ms;
	}
	
	boolean isAuth() {
		return this.ACCESS_TOKEN != null;
	}
	
	boolean isExpired() throws IOException {
		if( isAuth() ) {
			return ON_EXPIRATION < System.currentTimeMillis();
		} else {
			throw new IOException("Not logged in.");			
		}
	}
		
	void clearToken() {
		this.ACCESS_TOKEN = null;
		this.REFRESH_TOKEN = null;
		this.ON_EXPIRATION = 0;
	}
	
	void setToken(String tokenType, String accessToken, String refreshToken, long onExpiration) {
		this.ACCESS_TOKEN = tokenType + " " + accessToken;
		this.REFRESH_TOKEN = refreshToken;
		this.ON_EXPIRATION = onExpiration;
	}
	
	String getRefreshToken() {
		return this.REFRESH_TOKEN;
	}
	
	JsonObject request(RequestMethod requestMethod, ContentType contentType, final String apiPath, String params)
			throws IOException {

		String requestUrl = apiPath;
		if (requestMethod == null) {
			requestMethod = RequestMethod.GET;
		}
		if (params != null && params.length() > 0
				&& (requestMethod == RequestMethod.GET || requestMethod == RequestMethod.DELETE)) {
			requestUrl += "?" + params;
		}

		HttpsURLConnection conn;
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		InputStreamReader isr = null;

		try {
			final URL url = new URL(requestUrl);
			conn = (HttpsURLConnection) url.openConnection();
			
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			
			conn.setRequestMethod(requestMethod.toString());

			conn.setRequestProperty("Content-Type", contentType.getType());
			conn.setRequestProperty("charset", "utf-8");

			if (ACCESS_TOKEN != null) {
				conn.setRequestProperty("Authorization", ACCESS_TOKEN);
			}

			if (params != null && params.length() > 0 && requestMethod == RequestMethod.POST) {
				conn.setDoOutput(true);
				writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");

				writer.write(params);
				writer.flush();
			}

			final int responseCode = conn.getResponseCode();
			System.out.println(String.format("Sending '%s' request to URL : %s", requestMethod, requestUrl));
			System.out.println("Response Code : " + responseCode);
			System.out.println("===================================================================");

			if (responseCode < 400) {
				isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			} else {
				isr = new InputStreamReader(conn.getErrorStream(), "utf-8");
			}

			reader = new BufferedReader(isr);
			final StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			if (responseCode >= 400) {
				throw new IOException("HTTP status " + responseCode + " from " + requestUrl);
			}

			return JsonHelper.toJson(buffer.toString());

		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
				}
			if (reader != null)
				try {
					reader.close();
				} catch (Exception e) {
				}
			if (isr != null)
				try {
					isr.close();
				} catch (Exception e) {
				}
		}

	}
	
	JsonObject uploadFile(int team_number, File file) throws IOException {
		return uploadFiles(team_number, new File[]{file});
	}
	
	JsonObject uploadFile(int team_number, InputStream input_stream, String file_name) throws IOException {
		return uploadFiles(team_number, new InputStream[] {input_stream}, new String[] {file_name});
	}
	
	JsonObject uploadFiles(int team_number, File[] files) throws IOException {
		FileInputStream[] fisArray = new FileInputStream[files.length];
		String[] fileNames = new String[files.length];
		
		for(int i = 0; i < files.length; i++) {
			fisArray[i] = new FileInputStream(files[i]);
			fileNames[i] = files[i].getName();
		}
		
		JsonObject result = null;
		try {
			result = uploadFiles(team_number, fisArray, fileNames);
		} catch (IOException e) {
			throw e;
		} finally {
			for(FileInputStream fis:fisArray) {
				if(fis != null)
					try {
						fis.close();
					} catch (IOException ex) {
					}
			}
		}

		return result;
	}
	
	private final Pattern REGEXP_ILLEGAL_FILENAME = Pattern.compile("[:\\\\/%*?:|\"<>]");
	JsonObject uploadFiles(int team_number, InputStream[] input_streams, String[] file_names) throws IOException {
		
		if(input_streams.length != file_names.length)
			throw new IOException("InputStream and File name length is not match.");
		
		for(int i = 0 ; i < file_names.length; i++) {
			String fileName = file_names[i];
			if(fileName == null || fileName.trim().isEmpty())
				throw new IOException("Empty file name.");
			
			file_names[i] = REGEXP_ILLEGAL_FILENAME.matcher(fileName).replaceAll("_");
		}
		
		final String apiPath = Tmup4J.FILE_DOMAIN + "/v3/files/" + team_number;
		
		final String boundary = "---------------------------tmup4J" + System.currentTimeMillis();
		HttpsURLConnection conn = null;
		DataOutputStream dos = null;

		
		int bufferSize = 8 * 1024;
		byte[] buffer = new byte[bufferSize];
		// Response
		InputStreamReader isr = null;
		BufferedReader reader = null;

		// Request
		try {
			URL url = new URL(apiPath);
			conn = (HttpsURLConnection) url.openConnection();
			
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("Authorization", ACCESS_TOKEN);
			conn.setRequestProperty("Cache-Control", "no-cache");

			dos = new DataOutputStream(conn.getOutputStream());
			
			String boudaryFormat = "--" + boundary + "\r\n"
					+ "Content-Disposition: form-data; name=\"files[]\";" + " filename=\"%s\"\r\n"
					+ "Content-Type: %s\r\n"
					+ "\r\n";
			
			InputStream inputStream = null;
			
			for(int i = 0; i < input_streams.length; i++) {
				
				inputStream = new BufferedInputStream(input_streams[i], bufferSize); 
				
				dos.write(String.format(boudaryFormat, file_names[i], URLConnection.guessContentTypeFromStream(inputStream)).getBytes("utf-8"));
				
		        int bytesRead = inputStream.read(buffer);
		        while (bytesRead != -1) {
		            dos.write(buffer, 0, bytesRead);
		            bytesRead = inputStream.read(buffer);
		        }
				
				dos.writeBytes("\r\n");
			}
			
			dos.writeBytes("--" + boundary + "--" + "\r\n");
			dos.flush();

			final int responseCode = conn.getResponseCode();
			System.out.println("file upload Response Code : " + responseCode);
			System.out.println("============================================");
			if (responseCode == 200) {
				isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			} else {
				isr = new InputStreamReader(conn.getErrorStream(), "utf-8");
			}

			reader = new BufferedReader(isr);
			final StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			if (responseCode >= 400) {
				throw new IOException("HTTP status " + responseCode + " from " + apiPath);
			}

			return JsonHelper.toJson(sb.toString());
		} catch (IOException e) {
			throw e;
		} finally {
			if (dos != null)
				try {
					dos.close();
				} catch (IOException e) {
				}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
				}
			}
			conn.disconnect();
		}
	}
}
