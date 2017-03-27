package com.foamtec.controller;

import com.foamtec.AuthenCheck;
import com.foamtec.service.PlanningService;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    @RequestMapping(value = "/api/file/celestica", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(HttpServletResponse response) {
        try {
            String workingDir = System.getProperty("user.dir") + "/fileExcel/";
            File file = new File(workingDir + "celestica.xlsx");
            InputStream inputStream = new FileInputStream(file);
            response.setContentType("");
            response.setHeader("Content-Disposition", "inline;filename=" + file.getName());
            response.getOutputStream().write(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
