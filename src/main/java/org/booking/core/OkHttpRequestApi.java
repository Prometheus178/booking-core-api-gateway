package org.booking.core;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OkHttpRequestApi {
	private final OkHttpClient client = new OkHttpClient();

	public String putRequest(String apiUrl, String requestBody) throws IOException {
		RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
		Request request = new Request.Builder().url(apiUrl).put(body).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				throw new IOException("Unexpected response: " + response.code());
			}
		}
	}
}
