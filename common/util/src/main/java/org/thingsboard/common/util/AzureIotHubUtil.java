
package org.thingsboard.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;

@Slf4j
public final class AzureIotHubUtil {
    private static final String BASE_DIR_PATH = System.getProperty("user.dir");
    private static final String APP_DIR = "application";
    private static final String SRC_DIR = "src";
    private static final String MAIN_DIR = "main";
    private static final String DATA_DIR = "data";
    private static final String CERTS_DIR = "certs";
    private static final String AZURE_DIR = "azure";
    private static final String FILE_NAME = "DigiCertGlobalRootG2.crt.pem";

    private static final Path FULL_FILE_PATH;

    static {
        if (BASE_DIR_PATH.endsWith("bin")) {
            FULL_FILE_PATH = Paths.get(BASE_DIR_PATH.replaceAll("bin$", ""), DATA_DIR, CERTS_DIR, AZURE_DIR, FILE_NAME);
        } else if (BASE_DIR_PATH.endsWith("conf")) {
            FULL_FILE_PATH = Paths.get(BASE_DIR_PATH.replaceAll("conf$", ""), DATA_DIR, CERTS_DIR, AZURE_DIR, FILE_NAME);
        } else {
            FULL_FILE_PATH = Paths.get(BASE_DIR_PATH, APP_DIR, SRC_DIR, MAIN_DIR, DATA_DIR, CERTS_DIR, AZURE_DIR, FILE_NAME);
        }
    }

    private static final long SAS_TOKEN_VALID_SECS = 365 * 24 * 60 * 60;
    private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

    private static final String SAS_TOKEN_FORMAT = "SharedAccessSignature sr=%s&sig=%s&se=%s";

    private static final String USERNAME_FORMAT = "%s/%s/?api-version=2018-06-30";

    private AzureIotHubUtil() {
    }

    public static String buildUsername(String host, String deviceId) {
        return String.format(USERNAME_FORMAT, host, deviceId);
    }

    public static String buildSasToken(String host, String sasKey) {
        try {
            final String targetUri = URLEncoder.encode(host.toLowerCase(), "UTF-8");
            final long expiryTime = buildExpiresOn();
            String toSign = targetUri + "\n" + expiryTime;
            byte[] keyBytes = Base64.getDecoder().decode(sasKey.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
            String signature = URLEncoder.encode(Base64.getEncoder().encodeToString(rawHmac), "UTF-8");
            return String.format(SAS_TOKEN_FORMAT, targetUri, signature, expiryTime);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build SAS token!!!", e);
        }
    }

    private static long buildExpiresOn() {
        long expiresOnDate = System.currentTimeMillis();
        expiresOnDate += SAS_TOKEN_VALID_SECS * ONE_SECOND_IN_MILLISECONDS;
        return expiresOnDate / ONE_SECOND_IN_MILLISECONDS;
    }

    public static String getDefaultCaCert() {
        byte[] fileBytes;
        if (Files.exists(FULL_FILE_PATH)) {
            try {
                fileBytes = Files.readAllBytes(FULL_FILE_PATH);
            } catch (IOException e) {
                log.error("Failed to load Default CaCert file!!! [{}]", FULL_FILE_PATH, e);
                throw new RuntimeException("Failed to load Default CaCert file!!!");
            }
        } else {
            Path azureDirectory = FULL_FILE_PATH.getParent();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(azureDirectory)) {
                Iterator<Path> iterator = stream.iterator();
                if (iterator.hasNext()) {
                    Path firstFile = iterator.next();
                    fileBytes = Files.readAllBytes(firstFile);
                } else {
                    log.error("Default CaCert file not found in the directory [{}]!!!", azureDirectory);
                    throw new RuntimeException("Default CaCert file not found in the directory!!!");
                }
            } catch (IOException e) {
                log.error("Failed to load Default CaCert file from the directory [{}]!!!", azureDirectory, e);
                throw new RuntimeException("Failed to load Default CaCert file from the directory!!!");
            }
        }
        return new String(fileBytes);
    }

}
