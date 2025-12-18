package com.makers.moshpit.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cloudinary")
public class CloudinarySignatureController {

    private final Cloudinary cloudinary;

    @Value("${okta.cloudinary.api-key}")
    private String apiKey;

    @Value("${okta.cloudinary.cloud-name}")
    private String cloudName;

    @Value("${okta.cloudinary.api-secret}")
    private String apiSecret;

    public CloudinarySignatureController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @GetMapping("/signature")
    public Map<String, Object> signature() {
        long timestamp = System.currentTimeMillis() / 1000L;
        String folder = "moshpit/posts";

        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", folder);

        String signature = cloudinary.apiSignRequest(paramsToSign, apiSecret);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", timestamp);
        response.put("signature", signature);
        response.put("apiKey", apiKey);
        response.put("cloudName", cloudName);
        response.put("folder", folder);
        return response;
    }
}