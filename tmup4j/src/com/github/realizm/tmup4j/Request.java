package com.github.realizm.tmup4j;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

class Request {
	
	private String ACCESS_TOKEN = null;
	private String REFRESH_TOKEN = null;
	private long ON_EXPIRATION = 0;
	
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

	JsonObject uploadFile(final String apiPath, File file) throws IOException {
		
		File[] files = {file};
		
		return uploadFiles(apiPath, files);
	}
	
	JsonObject uploadFiles(final String apiPath, File[] files) throws IOException {

		final String boundary = "---------------------------tmup4J" + System.currentTimeMillis();
		HttpsURLConnection conn = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		// Response
		InputStreamReader isr = null;
		BufferedReader reader = null;

		// Request
		try {
			URL url = new URL(apiPath);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("Authorization", ACCESS_TOKEN);
			conn.setRequestProperty("Cache-Control", "no-cache");

			dos = new DataOutputStream(conn.getOutputStream());

			for (File file : files) {
				
				String boudaryString = "--" + boundary + "\r\n"
						+"Content-Disposition: form-data; name=\"files[]\";" + " filename=\"" + file.getName() + "\"\r\n"
						+ "\r\n";
				byte[] boundaryBytes = boudaryString.getBytes("utf-8");
				dos.write(boundaryBytes);
				
				fis = new FileInputStream(file);
				bytesAvailable = fis.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				bytesRead = fis.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fis.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fis.read(buffer, 0, bufferSize);
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
			if (fis != null)
				try {
					fis.close();
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
