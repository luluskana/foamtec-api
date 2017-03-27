package com.foamtec.service;

import com.foamtec.dao.MaterialPlanningDao;
import com.foamtec.domain.MaterialConvert;
import com.foamtec.domain.MaterialPlanning;
import com.foamtec.domain.MaterialSuccess;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlanningService {

    @Autowired
    private MaterialPlanningDao materialPlanningDao;

    private final Logger LOGGER = LoggerFactory.getLogger(PlanningService.class);

    public JSONObject create(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException, InvalidFormatException {
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet datatypeSheet = workbook.getSheetAt(0);
        int lastRow = datatypeSheet.getLastRowNum();
        LOGGER.debug("-= Total row {} =-", lastRow);
        int totalData = 0;
        for(int i = 1; i <= lastRow; i++) {
            Row currentRow = datatypeSheet.getRow(i);

            String materialFoamtec = null;
            Cell cell1 = currentRow.getCell(0);
            if(cell1 != null) {
                if(cell1.getCellType() == Cell.CELL_TYPE_STRING) {
                    materialFoamtec = cell1.getStringCellValue();
                } else {
                    materialFoamtec = "" + cell1.getNumericCellValue();
                }
            }

            String materialCustomer = null;
            Cell cell2 = currentRow.getCell(1);
            if(cell2 != null) {
                if(cell2.getCellType() == Cell.CELL_TYPE_STRING) {
                    materialCustomer = cell2.getStringCellValue();
                } else {
                    materialCustomer = "" + cell2.getNumericCellValue();
                }
            }

            String materialGroup = null;
            Cell cell3 = currentRow.getCell(2);
            if(cell3 != null) {
                if(cell3.getCellType() == Cell.CELL_TYPE_STRING) {
                    materialGroup = cell3.getStringCellValue();
                } else {
                    materialGroup = "" + cell2.getNumericCellValue();
                }
            }

            MaterialPlanning materialPlanning = new MaterialPlanning();
            materialPlanning.setMaterialFoamtec(materialFoamtec);
            materialPlanning.setMaterialCustomer(materialCustomer);
            materialPlanning.setMaterialGroup(materialGroup);

            materialPlanningDao.create(materialPlanning);

            LOGGER.debug("-= row " + i + " | " + materialFoamtec + " | " + materialCustomer + " | " + materialGroup + " =-");
            totalData = totalData + 1;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalData", totalData);
        return jsonObject;
    }

    public JSONObject convertFileCelestica(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException, InvalidFormatException {
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet datatypeSheet = workbook.getSheetAt(0);
        int lastRow = datatypeSheet.getLastRowNum();
        int totalData = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        List<MaterialConvert> materialConvertList = new ArrayList<>();
        String celesticaPart = null;
        String startDateStr = null;
        double qty = 0;
        Set<String> partCustomerSet = new HashSet<>();
        for(int i = 1; i <= lastRow; i++) {
            Row currentRow = datatypeSheet.getRow(i);
            Cell cell1 = currentRow.getCell(1);
            Cell cell2 = currentRow.getCell(3);
            Cell cell3 = currentRow.getCell(5);
            if(cell1.getCellType() != Cell.CELL_TYPE_BLANK && cell2.getCellType() != Cell.CELL_TYPE_BLANK && cell3.getCellType() != Cell.CELL_TYPE_BLANK) {
                System.out.println("cell1 = " + cell1 + ", cell2 = " + cell2 + ", cell3 = " + cell3);
                celesticaPart = cell1.getStringCellValue();
                startDateStr = cell2.getStringCellValue();
                qty = cell3.getNumericCellValue();
                partCustomerSet.add(celesticaPart);

                Date startDate = null;
                try {
                    startDate = formatter.parse(startDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                int month = cal.get(Calendar.MONTH);

                MaterialConvert materialConvert = new MaterialConvert();
                materialConvert.setCustomerPart(celesticaPart);
                materialConvert.setMonth(theMonth(month));
                materialConvert.setQty((int) qty);
                materialConvertList.add(materialConvert);
                totalData = totalData + 1;
            }
        }
        Map<String,MaterialPlanning> partFoamtecMap = new HashMap<>();
        for(String s : partCustomerSet) {
            partFoamtecMap.put(s,materialPlanningDao.findByCustomerPart(celesticaPart));
        }

        List<MaterialSuccess> materialSuccessList = new ArrayList<>();
        for(String s : partCustomerSet) {
            int total1 = 0;
            int total2 = 0;
            int total3 = 0;
            int total4 = 0;
            int total5 = 0;
            int total6 = 0;
            int total7 = 0;
            int total8 = 0;
            int total9 = 0;
            int total10 = 0;
            int total11 = 0;
            int total12 = 0;

            MaterialSuccess materialSuccess = new MaterialSuccess();
            materialSuccess.setCustomerPart(s);
            if(partFoamtecMap.get(s) == null) {
                materialSuccess.setFoamtecPart("N/A");
            } else {
                materialSuccess.setFoamtecPart(partFoamtecMap.get(s).getMaterialFoamtec());
            }
            for(MaterialConvert mc : materialConvertList) {
                if(mc.getCustomerPart().equals(s)) {

                    if(mc.getMonth().equals("January")) {
                        total1 = total1 + mc.getQty();
                    }
                    if(mc.getMonth().equals("February")) {
                        total2 = total2 + mc.getQty();
                    }
                    if(mc.getMonth().equals("March")) {
                        total3 = total3 + mc.getQty();
                    }
                    if(mc.getMonth().equals("April")) {
                        total4 = total4 + mc.getQty();
                    }
                    if(mc.getMonth().equals("May")) {
                        total5 = total5 + mc.getQty();
                    }
                    if(mc.getMonth().equals("June")) {
                        total6 = total6 + mc.getQty();
                    }
                    if(mc.getMonth().equals("July")) {
                        total7 = total7 + mc.getQty();
                    }
                    if(mc.getMonth().equals("August")) {
                        total8 = total8 + mc.getQty();
                    }
                    if(mc.getMonth().equals("September")) {
                        total9 = total9 + mc.getQty();
                    }
                    if(mc.getMonth().equals("October")) {
                        total10 = total10 + mc.getQty();
                    }
                    if(mc.getMonth().equals("November")) {
                        total11 = total11 + mc.getQty();
                    }
                    if(mc.getMonth().equals("December")) {
                        total12 = total12 + mc.getQty();
                    }
                }
            }
            materialSuccess.setJanuary(total1);
            materialSuccess.setFebruary(total2);
            materialSuccess.setMarch(total3);
            materialSuccess.setApril(total4);
            materialSuccess.setMay(total5);
            materialSuccess.setJune(total6);
            materialSuccess.setJuly(total7);
            materialSuccess.setAugust(total8);
            materialSuccess.setSeptember(total9);
            materialSuccess.setOctober(total10);
            materialSuccess.setNovember(total11);
            materialSuccess.setDecember(total12);

            materialSuccessList.add(materialSuccess);
        }

        for(MaterialSuccess materialSuccess : materialSuccessList) {
            System.out.println("Part : " + materialSuccess.getCustomerPart() + ", Code SAP : " + materialSuccess.getFoamtecPart() +
                    "|" + materialSuccess.getJanuary() + "|" +
                    "|" + materialSuccess.getFebruary() + "|" +
                    "|" + materialSuccess.getMarch() + "|" +
                    "|" + materialSuccess.getApril() + "|" +
                    "|" + materialSuccess.getMay() + "|" +
                    "|" + materialSuccess.getJune() + "|" +
                    "|" + materialSuccess.getJuly() + "|" +
                    "|" + materialSuccess.getAugust() + "|" +
                    "|" + materialSuccess.getSeptember() + "|" +
                    "|" + materialSuccess.getOctober() + "|" +
                    "|" + materialSuccess.getNovember() + "|" +
                    "|" + materialSuccess.getDecember() + "|"
            );
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Sheet sheet = wb.createSheet();
        Row row1 = sheet.createRow(0);

        Cell cell1 = row1.createCell(0);
        cell1.setCellStyle(style);
        cell1.setCellValue("PART");

        Cell cell2 = row1.createCell(1);
        cell2.setCellStyle(style);
        cell2.setCellValue("CODE SAP");

        Cell cell3 = row1.createCell(2);
        cell3.setCellStyle(style);
        cell3.setCellValue("January");

        Cell cell4 = row1.createCell(3);
        cell4.setCellStyle(style);
        cell4.setCellValue("February");

        Cell cell5 = row1.createCell(4);
        cell5.setCellStyle(style);
        cell5.setCellValue("March");

        Cell cell6 = row1.createCell(5);
        cell6.setCellStyle(style);
        cell6.setCellValue("April");

        Cell cell7 = row1.createCell(6);
        cell7.setCellStyle(style);
        cell7.setCellValue("May");

        Cell cell8 = row1.createCell(7);
        cell8.setCellStyle(style);
        cell8.setCellValue("June");

        Cell cell9 = row1.createCell(8);
        cell9.setCellStyle(style);
        cell9.setCellValue("July");

        Cell cell10 = row1.createCell(9);
        cell10.setCellStyle(style);
        cell10.setCellValue("August");

        Cell cell11 = row1.createCell(10);
        cell11.setCellStyle(style);
        cell11.setCellValue("September");

        Cell cell12 = row1.createCell(11);
        cell12.setCellStyle(style);
        cell12.setCellValue("October");

        Cell cell13 = row1.createCell(12);
        cell13.setCellStyle(style);
        cell13.setCellValue("November");

        Cell cell14 = row1.createCell(13);
        cell14.setCellStyle(style);
        cell14.setCellValue("December");

        int rowIndex = 1;
        for(MaterialSuccess materialSuccess : materialSuccessList) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(materialSuccess.getCustomerPart());
            row.createCell(1).setCellValue(materialSuccess.getFoamtecPart());
            row.createCell(2).setCellValue(materialSuccess.getJanuary());
            row.createCell(3).setCellValue(materialSuccess.getFebruary());
            row.createCell(4).setCellValue(materialSuccess.getMarch());
            row.createCell(5).setCellValue(materialSuccess.getApril());
            row.createCell(6).setCellValue(materialSuccess.getMay());
            row.createCell(7).setCellValue(materialSuccess.getJune());
            row.createCell(8).setCellValue(materialSuccess.getJuly());
            row.createCell(9).setCellValue(materialSuccess.getAugust());
            row.createCell(10).setCellValue(materialSuccess.getSeptember());
            row.createCell(11).setCellValue(materialSuccess.getOctober());
            row.createCell(12).setCellValue(materialSuccess.getNovember());
            row.createCell(13).setCellValue(materialSuccess.getDecember());
            rowIndex = rowIndex + 2;
        }

        for (int i=0; i < 14; i++){
            sheet.autoSizeColumn(i);
        }

        String workingDir = System.getProperty("user.dir") + "/fileExcel/";
        File convFile = new File(workingDir + "celestica.xlsx");
        convFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(convFile);
        wb.write(fos);
        fos.close();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalData", totalData);
        return jsonObject;
    }

    public JSONObject convertCelestica(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException, InvalidFormatException {
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet datatypeSheet = workbook.getSheetAt(0);
        int lastRow = datatypeSheet.getLastRowNum();
        LOGGER.debug("-= Total row {} =-", lastRow);
        int totalData = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        List<MaterialConvert> materialConvertList = new ArrayList<>();
        Set<String> partCustomerSet = new HashSet<>();

        for(int i = 1; i <= lastRow; i++) {
            Row currentRow = datatypeSheet.getRow(i);

            Cell cell1 = currentRow.getCell(1);
            Cell cell2 = currentRow.getCell(3);
            Cell cell3 = currentRow.getCell(5);

            String celesticaPart = null;
            String startDateStr = null;
            double qty = 0;
            if(cell1.getCellType() != Cell.CELL_TYPE_BLANK && cell2.getCellType() != Cell.CELL_TYPE_BLANK && cell3.getCellType() != Cell.CELL_TYPE_BLANK) {
                System.out.println("cell1 = " + cell1 + ", cell2 = " + cell2 + ", cell3 = " + cell3);
                celesticaPart = cell1.getStringCellValue();
                startDateStr = cell2.getStringCellValue();
                qty = cell3.getNumericCellValue();

                partCustomerSet.add(celesticaPart);

                Date startDate = null;
                try {
                    startDate = formatter.parse(startDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                int month = cal.get(Calendar.MONTH);

                MaterialPlanning materialPlanning = materialPlanningDao.findByCustomerPart(celesticaPart);

                MaterialConvert materialConvert = new MaterialConvert();
                materialConvert.setCustomerPart(celesticaPart);
                materialConvert.setMonth(theMonth(month));
                materialConvert.setQty((int) qty);
                if(materialPlanning == null) {
                    materialConvert.setFoamtecPart(null);
                } else {
                    materialConvert.setFoamtecPart(materialPlanning.getMaterialFoamtec());
                }

                materialConvertList.add(materialConvert);
            }

            totalData = totalData + 1;
        }

        List<MaterialSuccess> materialSuccessList = new ArrayList<>();
        for(String s : partCustomerSet) {
            int total1 = 0;
            int total2 = 0;
            int total3 = 0;
            int total4 = 0;
            int total5 = 0;
            int total6 = 0;
            int total7 = 0;
            int total8 = 0;
            int total9 = 0;
            int total10 = 0;
            int total11 = 0;
            int total12 = 0;

            MaterialSuccess materialSuccess = new MaterialSuccess();
            materialSuccess.setCustomerPart(s);

            for(MaterialConvert mc : materialConvertList) {
                if(mc.getCustomerPart().equals(s)) {
                    if(mc.getFoamtecPart() != null) {
                        materialSuccess.setFoamtecPart(mc.getFoamtecPart());
                    } else {
                        materialSuccess.setFoamtecPart("N/A");
                    }

                    if(mc.getMonth().equals("January")) {
                        total1 = total1 + mc.getQty();
                    }
                    if(mc.getMonth().equals("February")) {
                        total2 = total2 + mc.getQty();
                    }
                    if(mc.getMonth().equals("March")) {
                        total3 = total3 + mc.getQty();
                    }
                    if(mc.getMonth().equals("April")) {
                        total4 = total4 + mc.getQty();
                    }
                    if(mc.getMonth().equals("May")) {
                        total5 = total5 + mc.getQty();
                    }
                    if(mc.getMonth().equals("June")) {
                        total6 = total6 + mc.getQty();
                    }
                    if(mc.getMonth().equals("July")) {
                        total7 = total7 + mc.getQty();
                    }
                    if(mc.getMonth().equals("August")) {
                        total8 = total8 + mc.getQty();
                    }
                    if(mc.getMonth().equals("September")) {
                        total9 = total9 + mc.getQty();
                    }
                    if(mc.getMonth().equals("October")) {
                        total10 = total10 + mc.getQty();
                    }
                    if(mc.getMonth().equals("November")) {
                        total11 = total11 + mc.getQty();
                    }
                    if(mc.getMonth().equals("December")) {
                        total12 = total12 + mc.getQty();
                    }
                }
            }
            materialSuccess.setJanuary(total1);
            materialSuccess.setFebruary(total2);
            materialSuccess.setMarch(total3);
            materialSuccess.setApril(total4);
            materialSuccess.setMay(total5);
            materialSuccess.setJune(total6);
            materialSuccess.setJuly(total7);
            materialSuccess.setAugust(total8);
            materialSuccess.setSeptember(total9);
            materialSuccess.setOctober(total10);
            materialSuccess.setNovember(total11);
            materialSuccess.setDecember(total12);

            materialSuccessList.add(materialSuccess);
        }

        for(MaterialSuccess materialSuccess : materialSuccessList) {
            System.out.println("Part : " + materialSuccess.getCustomerPart() + ", Code SAP : " + materialSuccess.getFoamtecPart() +
                    "|" + materialSuccess.getJanuary() + "|" +
                    "|" + materialSuccess.getFebruary() + "|" +
                    "|" + materialSuccess.getMarch() + "|" +
                    "|" + materialSuccess.getApril() + "|" +
                    "|" + materialSuccess.getMay() + "|" +
                    "|" + materialSuccess.getJune() + "|" +
                    "|" + materialSuccess.getJuly() + "|" +
                    "|" + materialSuccess.getAugust() + "|" +
                    "|" + materialSuccess.getSeptember() + "|" +
                    "|" + materialSuccess.getOctober() + "|" +
                    "|" + materialSuccess.getNovember() + "|" +
                    "|" + materialSuccess.getDecember() + "|"
            );
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Sheet sheet = wb.createSheet();
        Row row1 = sheet.createRow(0);

        Cell cell1 = row1.createCell(0);
        cell1.setCellStyle(style);
        cell1.setCellValue("PART");

        Cell cell2 = row1.createCell(1);
        cell2.setCellStyle(style);
        cell2.setCellValue("CODE SAP");

        Cell cell3 = row1.createCell(2);
        cell3.setCellStyle(style);
        cell3.setCellValue("January");

        Cell cell4 = row1.createCell(3);
        cell4.setCellStyle(style);
        cell4.setCellValue("February");

        Cell cell5 = row1.createCell(4);
        cell5.setCellStyle(style);
        cell5.setCellValue("March");

        Cell cell6 = row1.createCell(5);
        cell6.setCellStyle(style);
        cell6.setCellValue("April");

        Cell cell7 = row1.createCell(6);
        cell7.setCellStyle(style);
        cell7.setCellValue("May");

        Cell cell8 = row1.createCell(7);
        cell8.setCellStyle(style);
        cell8.setCellValue("June");

        Cell cell9 = row1.createCell(8);
        cell9.setCellStyle(style);
        cell9.setCellValue("July");

        Cell cell10 = row1.createCell(9);
        cell10.setCellStyle(style);
        cell10.setCellValue("August");

        Cell cell11 = row1.createCell(10);
        cell11.setCellStyle(style);
        cell11.setCellValue("September");

        Cell cell12 = row1.createCell(11);
        cell12.setCellStyle(style);
        cell12.setCellValue("October");

        Cell cell13 = row1.createCell(12);
        cell13.setCellStyle(style);
        cell13.setCellValue("November");

        Cell cell14 = row1.createCell(13);
        cell14.setCellStyle(style);
        cell14.setCellValue("December");

        int rowIndex = 1;
        for(MaterialSuccess materialSuccess : materialSuccessList) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(materialSuccess.getCustomerPart());
            row.createCell(1).setCellValue(materialSuccess.getFoamtecPart());
            row.createCell(2).setCellValue(materialSuccess.getJanuary());
            row.createCell(3).setCellValue(materialSuccess.getFebruary());
            row.createCell(4).setCellValue(materialSuccess.getMarch());
            row.createCell(5).setCellValue(materialSuccess.getApril());
            row.createCell(6).setCellValue(materialSuccess.getMay());
            row.createCell(7).setCellValue(materialSuccess.getJune());
            row.createCell(8).setCellValue(materialSuccess.getJuly());
            row.createCell(9).setCellValue(materialSuccess.getAugust());
            row.createCell(10).setCellValue(materialSuccess.getSeptember());
            row.createCell(11).setCellValue(materialSuccess.getOctober());
            row.createCell(12).setCellValue(materialSuccess.getNovember());
            row.createCell(13).setCellValue(materialSuccess.getDecember());
            rowIndex = rowIndex + 2;
        }

        for (int i=0; i < 14; i++){
            sheet.autoSizeColumn(i);
        }

        String workingDir = System.getProperty("user.dir") + "/fileExcel/";
        File convFile = new File(workingDir + "celestica.xlsx");
        convFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(convFile);
        wb.write(fos);
        fos.close();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalData", totalData);
        return jsonObject;
    }

//    @RequestMapping(value = "/api/file", method = RequestMethod.GET)
//    @ResponseBody
//    public void downloadFile(HttpServletResponse response) {
//        try {
//            response.setContentType("");
//            response.setHeader("Content-Disposition", "inline;filename=" + fileData.getFileName());
//            response.getOutputStream().write(fileData.getDataFile());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
}
