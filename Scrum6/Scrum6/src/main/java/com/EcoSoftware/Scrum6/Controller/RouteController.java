package com.EcoSoftware.Scrum6.Controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "*") // permitir peticiones desde Angular u otros orígenes
public class RouteController {

    private static final String OSRM_BASE = "https://router.project-osrm.org/route/v1/driving/";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/**")
    public ResponseEntity<String> proxy(HttpServletRequest request) {
        // extraer lo que sigue a /api/route/
        String path = request.getRequestURI()
                .substring(request.getContextPath().length() + "/api/route/".length());

        String query = request.getQueryString();
        String target = OSRM_BASE + path + (query != null ? "?" + query : "");

        ResponseEntity<String> resp = restTemplate.getForEntity(target, String.class);
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }
}
