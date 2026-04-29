package com.westminster.smartcampus.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> discover() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("apiName", "Smart Campus Room and Sensor Management API");
        response.put("version", "v1");
        response.put("format", "application/json");

        Map<String, String> maintainer = new LinkedHashMap<>();
        maintainer.put("name", "Sithika Weerasinghe");
        maintainer.put("email", "sithika.20242022@iit.ac.lk");
        response.put("maintainer", maintainer);

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("root", "/api/v1");
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");
        endpoints.put("sensorReadings", "/api/v1/sensors/{sensorId}/readings");
        response.put("endpoints", endpoints);

        return response;
    }
}