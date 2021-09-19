package ntsonAuto;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ntson.enums.LanguageCode;
import ntson.model.EnJaSentenceRow;
import ntson.service.MyTextToSpeechService;
import ntson.service.MyTextToSpeechServicePremium;
import ntson.util.LogUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static ntson.util.ExceptionUtil.tryGet;
import static ntson.util.FileUtil.readEntireTextFile;
import static ntson.util.LogUtil.logExceptionAndReturnNull;
import static ntson.util.StringUtil.isNullOrBlank;

public class MainAuto_EnJpSentences {
    private static final String INPUT_FILE_PATH = "data/JP words.xlsx";
    private static final String INPUT_SHEET_NAME = "VNorENtoJP_tasks";
    private static final Integer MAX_ROW_COUNT_CONSIDER = 10000;
    private static final Logger logger = LoggerFactory.getLogger(MainAuto_EnJpSentences.class);
    private static final MyTextToSpeechService myTextToSpeechService = new MyTextToSpeechService();
    private static final MyTextToSpeechServicePremium myTextToSpeechServicePremium = new MyTextToSpeechServicePremium(
                myTextToSpeechService.getTextToSpeechClient() // Reuse client.
    );
    public static void main(String[] args) throws Exception {
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
            String lessonId = tryGet(() -> row.getCell(0).getStringCellValue(), LogUtil::ignoreExceptionAndReturnNull);
            String englishRawText = tryGet(() -> row.getCell(1).getStringCellValue(), LogUtil::ignoreExceptionAndReturnNull);
            String japaneseRawText = tryGet(() -> row.getCell(2).getStringCellValue(), LogUtil::ignoreExceptionAndReturnNull);
            if (isNullOrBlank(englishRawText) || isNullOrBlank(japaneseRawText)) {
                continue;
            }
            EnJaSentenceRow enJaSentenceRow = new EnJaSentenceRow(lessonId, englishRawText, japaneseRawText);
            listEnJaSentenceRow.add(enJaSentenceRow);
        }
        logger.info("listEnJaSentenceRow={}", listEnJaSentenceRow);

        for (EnJaSentenceRow enJaSentenceRow : listEnJaSentenceRow) {
            String japaneseNoSpaceText = enJaSentenceRow.getJapaneseNoSpaceText();
            String filePathTTSJapanese
                    = myTextToSpeechServicePremium.processTextToSpeechOrCachedWaveNetJp(japaneseNoSpaceText, AudioEncoding.OGG_OPUS);
            logger.info("filePathTTSJapanese={}", filePathTTSJapanese);
            sleepStandard();
            //--------------
            String englishTrimmedText = enJaSentenceRow.getEnglishTrimmedText();
            String filePathTTSEnglish
                    = myTextToSpeechService.processTextToSpeechOrCachedV2(englishTrimmedText, LanguageCode.EN_US);
            logger.info("filePathTTSEnglish={}", filePathTTSEnglish);
            sleepStandard();
        }
    }
    private static void sleepStandard() {
        try {
            Thread.sleep(1000/*TODO:DEMO*/);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
