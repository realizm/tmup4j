package pe.kr.realizm.tmup4j;

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

class RestHelper {

	private static String AUTHORIZATION = null;

	protected void setAuthorization(String Authorization) {
		RestHelper.AUTHORIZATION = Authorization;
	}

	public JsonObject request(RequestMethod requestMethod, ContentType contentType, final String apiPath, String params)
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

			if (AUTHORIZATION != null) {
				System.out.println("AUTHORIZATION :" + AUTHORIZATION);
				conn.setRequestProperty("Authorization", AUTHORIZATION);
			}

			if (params != null && params.length() > 0 && requestMethod == RequestMethod.POST) {
				conn.setDoOutput(true);
				writer = new OutputStreamWriter(conn.getOutputStream());

				writer.write(params);
				writer.flush();
			}

			final int responseCode = conn.getResponseCode();
			System.out.println(String.format("Sending '%s' request to URL : %s", requestMethod, requestUrl));
			System.out.println("Response Code : " + responseCode);

			if (responseCode < 400) {
				isr = new InputStreamReader(conn.getInputStream());
			} else {
				isr = new InputStreamReader(conn.getErrorStream());
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

	public JsonObject uploadFiles(final String apiPath, File[] files) throws IOException {

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
			conn.setRequestProperty("Authorization", AUTHORIZATION);
			conn.setRequestProperty("Cache-Control", "no-cache");

			dos = new DataOutputStream(conn.getOutputStream());

			for (File file : files) {
				dos.writeBytes("--" + boundary + "\r\n");
				dos.writeBytes("Content-Disposition: form-data; name=\"files[]\";" + " filename=\"" + file.getName()
						+ "\"" + "\r\n");
				dos.writeBytes("\r\n");
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
			if (responseCode == 200) {
				isr = new InputStreamReader(conn.getInputStream());
			} else {
				isr = new InputStreamReader(conn.getErrorStream());
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
