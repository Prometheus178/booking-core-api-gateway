package org.booking.core.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.booking.core.OkHttpRequestApi;
import org.booking.core.request.TokenRequest;
import org.booking.core.response.LoggedInResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log
@RequiredArgsConstructor
@Service
public class AuthenticationService {
	String AUTH_URL = "http://booking-core-authentication:8081/api/v1/auth/inner/authenticate";

	private final OkHttpRequestApi okHttpRequestApi;

	private final Gson gson = new Gson();

	public LoggedInResponse authenticate(String token) throws IOException {
		TokenRequest tokenRequest = new TokenRequest(token);
		String response = okHttpRequestApi.putRequest(AUTH_URL, gson.toJson(tokenRequest));
		return gson.fromJson(response, LoggedInResponse.class);
	}
}
