//package com.assignment.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class SecurityRestTemplateService {
//    RestTemplate restTemplate = new RestTemplate();
//
//    public JsonNode get(String url){
//        return this.request(url, HttpMethod.GET, null);
//    }
//
//    public JsonNode post(String url, Object data){
//        return this.request(url, HttpMethod.POST, data);
//    }
//
//    public JsonNode put(String url, Object data){
//        return this.request(url, HttpMethod.PUT, data);
//    }
//
//    public JsonNode delete(String url){
//        return this.request(url, HttpMethod.DELETE, null);
//    }
//
//    private JsonNode request(String url, HttpMethod method, Object data){
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic dXNlcjI6MTIz");
//        HttpEntity<Object> entity = new HttpEntity<>(data, headers);
//        ResponseEntity<JsonNode> response = restTemplate.exchange(url, method, entity, JsonNode.class);
//        return response.getBody();
//    }
//}
