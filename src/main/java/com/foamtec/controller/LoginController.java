package com.foamtec.controller;

import com.foamtec.dao.AppUserDao;
import com.foamtec.domain.AppUser;
import com.foamtec.domain.RoleName;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import java.util.Calendar;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private AppUserDao appUserDao;

    final String SECRET_KEY = "ZXCVBHGFDSA";

    @PostMapping(value = "/login", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> login(MultipartHttpServletRequest multipartHttpServletRequest) throws ServletException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        String username = multipartHttpServletRequest.getParameter("username");
        String password = multipartHttpServletRequest.getParameter("password");

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 3);
        Date endDate = c.getTime();

        if (username.equals("admin") && password.equals("pacific03srv")) {
            String token = "Bearer " + Jwts.builder()
                    .setSubject("admin")
                    .claim("roles", "admin")
                    .setIssuedAt(new Date())
                    .setExpiration(endDate)
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            jsonObject.put("name", "Admin");
            jsonObject.put("roles", "admin,");
            return new ResponseEntity<String>(jsonObject.toString(), headers, HttpStatus.OK);
        }

        AppUser appUser = appUserDao.findByUsernameAndPassword(username, password);

        if (appUser != null) {
            String roles = "";
            for(RoleName roleName: appUser.getRoleNames()) {
                roles = roles + roleName.getName() + ",";
            }
            String token = "Bearer " + Jwts.builder()
                    .setSubject(appUser.getUsername())
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(endDate)
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            jsonObject.put("name", appUser.getName());
            jsonObject.put("roles", roles);
            return new ResponseEntity<String>(jsonObject.toString(), headers, HttpStatus.OK);
        }
        throw new ServletException("Invalid login");
    }
}
