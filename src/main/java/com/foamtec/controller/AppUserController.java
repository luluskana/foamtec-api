package com.foamtec.controller;

import com.foamtec.AuthenCheck;
import com.foamtec.service.AppUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
public class AppUserController {

    @Autowired
    private AuthenCheck authenCheck;

    @Autowired
    private AppUserService appUserService;

    @PostMapping(value = "/api/appuser/create", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> appUserCreate(MultipartHttpServletRequest multipartHttpServletRequest) throws ServletException, IOException {
        MultipartHttpServletRequest multiServletRequest = authenCheck.jwtFilter(multipartHttpServletRequest);
        Claims claims = (Claims) multiServletRequest.getAttribute("claims");
        JSONObject jsonObject = appUserService.create(multipartHttpServletRequest, claims);
        return new ResponseEntity<String>(jsonObject.toString(), authenCheck.getHttpHeaders(), HttpStatus.OK);
    }
}
