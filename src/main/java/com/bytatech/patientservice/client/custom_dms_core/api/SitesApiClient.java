package com.bytatech.patientservice.client.custom_dms_core.api;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name="${customDmsCore.name:customDmsCore}", url="${customDmsCore.url:https://zm4u0g.trial.alfresco.com/alfresco/api/-default-/public/alfresco/versions/1}")
public interface SitesApiClient extends SitesApi {
}