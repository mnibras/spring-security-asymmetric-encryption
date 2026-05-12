package com.nibras.app.auth;

import com.nibras.app.auth.request.AuthenticationRequest;
import com.nibras.app.auth.request.RefreshRequest;
import com.nibras.app.auth.request.RegistrationRequest;
import com.nibras.app.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    void register(RegistrationRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest req);

}
