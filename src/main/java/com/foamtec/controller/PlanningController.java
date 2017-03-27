package com.foamtec.controller;

import com.foamtec.AuthenCheck;
import com.foamtec.service.PlanningService;
import io.jsonwebtoken.Claims;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
public class PlanningController {

    @Autowired
    private PlanningService planningService;

    @Autowired
    private AuthenCheck authenCheck;

    @PostMapping(value = "/api/planning/loaddata", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> uploadData(MultipartHttpServletRequest multipartHttpServletRequest) throws ServletException, IOException, InvalidFormatException {
        JSONObject jsonObject = planningService.create(multipartHttpServletRequest);
        return new ResponseEntity<String>(jsonObject.toString(), authenCheck.getHttpHeaders(), HttpStatus.OK);
    }

    @PostMapping(value = "/api/planning/celestica", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> uploadDataCelestica(MultipartHttpServletRequest multipartHttpServletRequest) throws ServletException, IOException, InvalidFormatException {
        JSONObject jsonObject = planningService.convertFileCelestica(multipartHttpServletRequest);
        return new ResponseEntity<String>(jsonObject.toString(), authenCheck.getHttpHeaders(), HttpStatus.OK);
    }
}
