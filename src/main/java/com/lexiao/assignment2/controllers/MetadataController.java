package com.lexiao.assignment2.controllers;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MetadataController {

    private static final String BASE_URL = "http://169.254.169.254/latest/meta-data/";
    private String instanceId;
    private String availabilityZoneId;

    @PostConstruct
    public void init() {
        this.instanceId = fetchMetadata("instance-id");
        this.availabilityZoneId = fetchMetadata("placement/availability-zone-id");
    }

    private String fetchMetadata(String path) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    return reader.readLine();
                }
            } else {
                System.err.println("Metadata fetch failed: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching metadata: " + e.getMessage());
        }
        return "unavailable";
    }

    @GetMapping("/v2/metadata")
    public Map<String, String> getMetadata() {
        Map<String, String> result = new HashMap<>();
        result.put("aws_instance_id", instanceId);
        result.put("aws_availability_zone_id", availabilityZoneId);
        return result;
    }
}
