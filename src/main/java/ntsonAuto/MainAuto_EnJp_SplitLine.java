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
import ntson.util.StringFancy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static ntson.service.MyFileService.writeToTextFile;
import static ntson.util.ExceptionUtil.tryGet;
import static ntson.util.FileUtil.createDirectoriesOptional;
import static ntson.util.FileUtil.readEntireTextFile;
import static ntson.util.LogUtil.logExceptionAndReturnNull;
import static ntson.util.StringUtil.isNullOrBlank;

public class MainAuto_EnJp_SplitLine {
    static {
        new MyPasswordService().checkProgramPassword();
    }
    private static final String INPUT_FILE_PATH = "data/JP words.xlsx";
    private static final String INPUT_SHEET_NAME = "VNorENtoJP_tasks";
    private static final Integer MAX_ROW_COUNT_CONSIDER = 10000;
    private static final Logger logger = LoggerFactory.getLogger(MainAuto_EnJpSentences.class);
    // private static final MyFileService myFileService = new MyFileService();
    private static final MyTextToSpeechService myTextToSpeechService = new MyTextToSpeechService();
    private static final MyTextToSpeechServicePremium myTextToSpeechServicePremium = new MyTextToSpeechServicePremium(
                myTextToSpeechService.getTextToSpeechClient() // Reuse client.
    );
    public static void main(String[] args) throws Exception {
        // Create audio output folders:
        createDirectoriesOptional(MyFileService.OUTPUT_AUDIO_FOLDER_PATH_FEMALE_EN_US,
                (ioException, pathStr) -> logger.error("Error while creating/checking folder {}", pathStr, ioException));
        createDirectoriesOptional(MyFileService.OUTPUT_AUDIO_FOLDER_PATH_FEMALE_JA_JP,
                (ioException, pathStr) -> logger.error("Error while creating/checking folder {}", pathStr, ioException));
        createDirectoriesOptional(MyFileService.OUTPUT_AUDIO_FOLDER_PATH_FEMALE_VI_VN,
                (ioException, pathStr) -> logger.error("Error while creating/checking folder {}", pathStr, ioException));

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
            /*
            String lessonId = tryGet(() -> row.getCell(0).getStringCellValue(), LogUtil::ignoreExceptionAndReturnNull);
            String englishRawText = tryGet(() -> row.getCell(1).getStringCellValue(), LogUtil::ignoreExceptionAndReturnNull);
            String japaneseRawText = tryGet(() -> row.getCell(2).getStringCellValue(), LogUtil::ignoreExceptionAndReturnNull);
            if (isNullOrBlank(englishRawText) || isNullOrBlank(japaneseRawText)) {
                continue;
            }
            EnJaSentenceRow enJaSentenceRow = new EnJaSentenceRow(lessonId, englishRawText, japaneseRawText);
            listEnJaSentenceRow.add(enJaSentenceRow);
            */
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
                enJaSentenceRow.japaneseRepresentativeText = representativeJapaneseText;
            }
            listEnJaSentenceRow.add(enJaSentenceRow);
        }
        logger.info("Done reading excel file! listEnJaSentenceRow.size()={}", listEnJaSentenceRow.size());

        // Process text to speech.
        int rowExcelProcessed = 0;
        for (EnJaSentenceRow enJaSentenceRow : listEnJaSentenceRow) {
            String japaneseNoSpaceText = enJaSentenceRow.getJapaneseNoSpaceText();
            String filePathTTSJapanese
                    = myTextToSpeechServicePremium.processTextToSpeechOrCachedWaveNetJp(japaneseNoSpaceText, AudioEncoding.OGG_OPUS);
            enJaSentenceRow.japaneseAudioFileName = filePathTTSJapanese;
            String[] japaneseNoSpaceText_splitLines = japaneseNoSpaceText.split("\\r?\\n");
            for (String sentenceText : japaneseNoSpaceText_splitLines) {
                String sentenceAudioFilePath
                    = myTextToSpeechServicePremium.processTextToSpeechOrCachedWaveNetJp(sentenceText, AudioEncoding.OGG_OPUS);
                enJaSentenceRow.addSentenceJapanese(sentenceText, sentenceAudioFilePath);
            }
            //--------------
            String englishTrimmedText = enJaSentenceRow.getEnglishTrimmedText();
            String filePathTTSEnglish
                    = myTextToSpeechService.processTextToSpeechOrCachedV2(englishTrimmedText, LanguageCode.EN_US);
            enJaSentenceRow.englishAudioFileName = filePathTTSEnglish;
            String[] englishNoSpaceText_splitLines = englishTrimmedText.split("\\r?\\n");
            for (String sentenceText : englishNoSpaceText_splitLines) {
                String sentenceAudioFilePath
                    = myTextToSpeechService.processTextToSpeechOrCachedV2(sentenceText, LanguageCode.EN_US);
                enJaSentenceRow.addSentenceEnglish(sentenceText, sentenceAudioFilePath);
            }
            System.out.println("Processed row " + (rowExcelProcessed++)); // Use sout to avoid color! (?)
        }

        // Convert the list into a JSON-string.
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent(StringFancy.SPACE2);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        gson.toJson(listEnJaSentenceRow, Object.class, jsonWriter);
        jsonWriter.close();
        String json = stringWriter.toString();
        System.out.println(json); // Should use this instead of logger!

        // Write the JSON-string to a File.
        writeToTextFile(
                Paths.get(MyFileService.OUTPUT_FOLDER_NAME),
                MyFileService.OUTPUT_ENJASENTENCES_JSON_FILENAME,
                json);
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
