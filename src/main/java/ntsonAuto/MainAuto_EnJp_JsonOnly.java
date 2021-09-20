package ntsonAuto;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonWriter;
import ntson.enums.LanguageCode;
import ntson.model.EnJaSentenceRow;
import ntson.service.MyFileService;
import ntson.service.MyPasswordService;
import ntson.service.MyTextToSpeechService;
import ntson.service.MyTextToSpeechServicePremium;
import ntson.util.LogUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static ntson.util.ExceptionUtil.tryGet;
import static ntson.util.FileUtil.createDirectoriesOptional;
import static ntson.util.FileUtil.readEntireTextFile;
import static ntson.util.LogUtil.logExceptionAndReturnNull;
import static ntson.util.StringUtil.isNullOrBlank;

public class MainAuto_EnJp_JsonOnly {
    private static final String INPUT_FILE_PATH = "data/JP words.xlsx";
    private static final String INPUT_SHEET_NAME = "VNorENtoJP_tasks";
    private static final Integer MAX_ROW_COUNT_CONSIDER = 10000;
    private static final Logger logger = LoggerFactory.getLogger(MainAuto_EnJpSentences.class);
    public static void main(String[] args) throws Exception {
        // Read input excel file:
        FileInputStream inputFile = new FileInputStream(INPUT_FILE_PATH);
        Workbook workbook = new XSSFWorkbook(inputFile);
        Sheet inputSheet = workbook.getSheet(INPUT_SHEET_NAME);

        List<EnJaSentenceRow> listEnJaSentenceRow = new ArrayList<>();
        int rowCount = 0;
        for (Row row : inputSheet) {
            rowCount++;
            if (rowCount > MAX_ROW_COUNT_CONSIDER) {
                break;
            }
            String lessonId = tryReadCellAsString(row, 0);
            String englishRawText = tryReadCellAsString(row, 1);
            String japaneseRawText = tryReadCellAsString(row, 2);
            if (isNullOrBlank(englishRawText) || isNullOrBlank(japaneseRawText)) {
                continue;
            }
            String representativeJapaneseText = null;
            Object isRepresentativeCellExist = tryReadCell(row, 4);
            if (Objects.equals(true, isRepresentativeCellExist)) {
                representativeJapaneseText = tryReadCellAsString(row, 3);
                if (isNullOrBlank(representativeJapaneseText)) {
                    representativeJapaneseText = null;
                } else {
                    representativeJapaneseText = representativeJapaneseText.trim();
                }
            }
            EnJaSentenceRow enJaSentenceRow = new EnJaSentenceRow(lessonId, englishRawText, japaneseRawText);
            if (representativeJapaneseText != null) {
                enJaSentenceRow.japaneseRepresentative = representativeJapaneseText;
            }
            listEnJaSentenceRow.add(enJaSentenceRow);
        }
        logger.info("listEnJaSentenceRow={}", listEnJaSentenceRow);

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent("   ");
        Gson gson = new GsonBuilder()
                .create();
        gson.toJson(listEnJaSentenceRow, Object.class, jsonWriter);
        jsonWriter.close();

        System.out.println(stringWriter.toString());
    }
    private static Object tryReadCell(Row row, int cellnum) {
        if (row != null) {
            Cell cell = row.getCell(cellnum);
            if (cell != null) {
                CellType cellType = cell.getCellTypeEnum();
                switch (cellType) {
                    case STRING: {
                        return cell.getStringCellValue();
                    }
                    case BOOLEAN: {
                        return cell.getBooleanCellValue();
                    }
                    case NUMERIC: {
                        return cell.getNumericCellValue();
                    }
                }
            }
        }
        return null;
    }
    private static String tryReadCellAsString(Row row, int cellnum) {
        Object cellValue = tryReadCell(row, cellnum);
        if (cellValue == null) {
            return null;
        } else {
            return cellValue.toString();
        }
    }
}
