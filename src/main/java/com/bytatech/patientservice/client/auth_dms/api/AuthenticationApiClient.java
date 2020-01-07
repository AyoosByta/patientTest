package com.bytatech.patientservice.client.auth_dms.api;

import org.springframework.cloud.openfeign.FeignClient;
import com.bytatech.patientservice.client.auth_dms.ClientConfiguration;

@FeignClient(name="${authDMS.name:authDMS}", url="${authDMS.url:https://zm4u0g.trial.alfresco.com/alfresco/api/-default-/public/authentication/versions/1}", configuration = ClientConfiguration.class)
public interface AuthenticationApiClient extends AuthenticationApi {
}