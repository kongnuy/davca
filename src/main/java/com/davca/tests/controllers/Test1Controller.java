package com.davca.tests.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import javax.servlet.http.HttpServletResponse;

import com.davca.tests.entities.InfoFichier;
import com.davca.tests.repositories.InfoFichierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/v1/test1")
@Api(description = "manage all test 1 actions : Noubliez Pas de soumettre un fichier pas trop lourd pour le endpoint de chargement en BD (/v1/test1/load/to/database)")
public class Test1Controller {

  @Autowired
  InfoFichierRepository repo;

  @GetMapping("/jpegs")
  @ApiOperation(value = "Écrire une requête pour extraire les lignes contenant les fichier jpg ou jpeg")
  public Iterable<InfoFichier> allByJPEG() {
    return repo.findByType("JPG-Datei");
  }

  @GetMapping("/pngs/tiffs/bmps/gifs")
  @ApiOperation(value = "Écrire une requête pour extraire les lignes contenant les fichier images png, tiff, bmp, gif")
  public Iterable<InfoFichier> allByPngTiffBmpGif() {
    return repo.findByTypeOrTypeOrTypeOrType("PNG-Datei", "TIFF-Datei", "BMP-Datei", "GIF-Datei");
  }

  @GetMapping("/jpegs/export")
  @ApiOperation(value = "Générer deux fichier csv comme output pour 2- et 3- : JPEGs")
  public void exportJPEGToCSV(HttpServletResponse response) throws IOException {
    List<InfoFichier> list = (List<InfoFichier>) repo.findByType("JPG-Datei");
    getCsv(list, response, "jpegs");
  }

  @GetMapping("/pngs/tiffs/bmps/gifs/export")
  @ApiOperation(value = "Générer deux fichier csv comme output pour 2- et 3- : PNGs, TIFFs, BMPs, GIFs")
  public void exportPngsTiffsBmpsGifsToCSV(HttpServletResponse response) throws IOException {
    List<InfoFichier> list = (List<InfoFichier>) repo.findByTypeOrTypeOrTypeOrType("PNG-Datei", "TIFF-Datei",
        "BMP-Datei", "GIF-Datei");
    getCsv(list, response, "pngs_tiffs_bmps_gifs");
  }

  @PostMapping("/load/to/database")
  @ApiOperation(value = "Importer les données dans une base de données (MySQL, MS SQL server ou oracle)")
  public DeferredResult<Iterable<InfoFichier>> loadFileToDb(@RequestParam("file") MultipartFile multipartFile) {
    System.out.println("==== Received async-deferredresult request ======");
    DeferredResult<Iterable<InfoFichier>> output = new DeferredResult<>();
    ForkJoinPool.commonPool().submit(() -> {
      System.out.println("==== Processing in separate thread ======");
      List<InfoFichier> list = new ArrayList<>();
      try {
        Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        Sheet datatypeSheet = workbook.getSheetAt(1);
        Iterator<Row> iterator = datatypeSheet.iterator();
        while (iterator.hasNext()) {
          try {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() != 0) {
              InfoFichier f = new InfoFichier();
              InfoFichier exists = repo.findByPath(currentRow.getCell(3).getStringCellValue());
              f.setDrive(currentRow.getCell(0).getStringCellValue());
              f.setFilename(currentRow.getCell(1).getStringCellValue());
              f.setFolder(currentRow.getCell(2).getStringCellValue());
              f.setPath(currentRow.getCell(3).getStringCellValue());
              f.setSize(currentRow.getCell(4).getNumericCellValue());
              f.setType(currentRow.getCell(5).getStringCellValue());
              f.setCreatedAt(currentRow.getCell(6).getDateCellValue());
              f.setUpdatedAt(currentRow.getCell(7).getDateCellValue());
              f.setLastAccess(currentRow.getCell(8).getDateCellValue());
              list.add(f);
              if (exists == null) {
                repo.save(f);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        workbook.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      output.setResult(list);
    });

    return output;
  }

  @PostMapping("/jpegs/load/to/csv")
  @ApiOperation(value = "Écrire un programme Java qui lit le fichier initial et génère les deux csv demandés : JPEGS")
  public void loadFileToJPEGsCSVs(@RequestParam("file") MultipartFile multipartFile, HttpServletResponse response) {
    List<InfoFichier> list = new ArrayList<>();
    try {
      Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
      Sheet datatypeSheet = workbook.getSheetAt(1);
      Iterator<Row> iterator = datatypeSheet.iterator();
      while (iterator.hasNext()) {
        try {
          Row currentRow = iterator.next();
          if (currentRow.getRowNum() != 0) {
            if (currentRow.getCell(1).getStringCellValue().toLowerCase().contains(".jpg")
                || currentRow.getCell(1).getStringCellValue().toLowerCase().contains(".jpeg")) {
              InfoFichier f = new InfoFichier();
              f.setId((long) currentRow.getRowNum());
              f.setDrive(currentRow.getCell(0).getStringCellValue());
              f.setFilename(currentRow.getCell(1).getStringCellValue());
              f.setFolder(currentRow.getCell(2).getStringCellValue());
              f.setPath(currentRow.getCell(3).getStringCellValue());
              f.setSize(currentRow.getCell(4).getNumericCellValue());
              f.setType(currentRow.getCell(5).getStringCellValue());
              f.setCreatedAt(currentRow.getCell(6).getDateCellValue());
              f.setUpdatedAt(currentRow.getCell(7).getDateCellValue());
              f.setLastAccess(currentRow.getCell(8).getDateCellValue());
              list.add(f);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      workbook.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {

      getCsv(list, response, "jpegs");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @PostMapping("/pngs/tiffs/bmps/gifs/load/to/csv")
  @ApiOperation(value = "Écrire un programme Java qui lit le fichier initial et génère les deux csv demandés : PNGs, TIFFs, BMPs, GIFs")
  public void loadFileToCSVs(@RequestParam("file") MultipartFile multipartFile, HttpServletResponse response) {
    List<InfoFichier> list = new ArrayList<>();
    try {
      Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
      Sheet datatypeSheet = workbook.getSheetAt(1);
      Iterator<Row> iterator = datatypeSheet.iterator();
      while (iterator.hasNext()) {
        try {
          Row currentRow = iterator.next();
          if (currentRow.getRowNum() != 0) {
            if (currentRow.getCell(1).getStringCellValue().toLowerCase().contains(".png")
                || currentRow.getCell(1).getStringCellValue().toLowerCase().contains(".tiff")
                || currentRow.getCell(1).getStringCellValue().toLowerCase().contains(".bmp")
                || currentRow.getCell(1).getStringCellValue().toLowerCase().contains(".gif")) {
              InfoFichier f = new InfoFichier();
              f.setId((long) currentRow.getRowNum());
              f.setDrive(currentRow.getCell(0).getStringCellValue());
              f.setFilename(currentRow.getCell(1).getStringCellValue());
              f.setFolder(currentRow.getCell(2).getStringCellValue());
              f.setPath(currentRow.getCell(3).getStringCellValue());
              f.setSize(currentRow.getCell(4).getNumericCellValue());
              f.setType(currentRow.getCell(5).getStringCellValue());
              f.setCreatedAt(currentRow.getCell(6).getDateCellValue());
              f.setUpdatedAt(currentRow.getCell(7).getDateCellValue());
              f.setLastAccess(currentRow.getCell(8).getDateCellValue());
              list.add(f);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      workbook.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      getCsv(list, response, "pngs_tiffs_bmps_gifs");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void getCsv(List<InfoFichier> list, HttpServletResponse response, String prefix) throws IOException {
    response.setContentType("text/csv; charset=UTF-8");
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    String currentDateTime = dateFormatter.format(new Date());

    String headerKey = "Content-Disposition";
    String headerValue = "attachment; filename=" + prefix + "_" + currentDateTime + ".csv";
    response.setHeader(headerKey, headerValue);

    ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
    String[] csvHeader = { "ID", "Drive", "Nom", "Dossier", "Chemin", "Taille", "Type", "Chemin", "Date de création",
        "Dernière modification", "Dernier accès" };
    String[] nameMapping = { "id", "drive", "filename", "folder", "path", "size", "type", "createdAt", "updatedAt",
        "lastAccess" };

    csvWriter.writeHeader(csvHeader);

    for (InfoFichier item : list) {
      csvWriter.write(item, nameMapping);
    }

    csvWriter.close();
  }
}