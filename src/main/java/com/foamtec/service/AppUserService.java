package com.foamtec.service;

import com.foamtec.dao.AppUserDao;
import com.foamtec.domain.AppUser;
import io.jsonwebtoken.Claims;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Date;

@Service
public class AppUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(AppUserService.class);

    @Autowired
    private AppUserDao appUserDao;

    public JSONObject create(MultipartHttpServletRequest multipartHttpServletRequest, Claims claims) {
        String employeeID = multipartHttpServletRequest.getParameter("employeeID");
        String username = multipartHttpServletRequest.getParameter("username");
        String password = multipartHttpServletRequest.getParameter("password");
        String name = multipartHttpServletRequest.getParameter("name");
        String sex = multipartHttpServletRequest.getParameter("sex");
        String department = multipartHttpServletRequest.getParameter("department");
        String emailAddress = multipartHttpServletRequest.getParameter("emailAddress");
        String phoneNumber = multipartHttpServletRequest.getParameter("phoneNumber");
        AppUser createBy = appUserDao.findByUsername(claims.getSubject());
        AppUser appUser = new AppUser();
        if(createBy == null) {
            appUser.setCreateBy("Admin");
        } else {
            appUser.setCreateBy(claims.getSubject());
        }
        appUser.setCreateDate(new Date());
        appUser.setEmployeeID(employeeID);
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setName(name);
        appUser.setSex(sex);
        appUser.setDepartment(department);
        appUser.setEmailAddress(emailAddress);
        appUser.setPhoneNumber(phoneNumber);
        appUserDao.create(appUser);
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("id", appUser.getId());
        return jsonObject;
    }
}
