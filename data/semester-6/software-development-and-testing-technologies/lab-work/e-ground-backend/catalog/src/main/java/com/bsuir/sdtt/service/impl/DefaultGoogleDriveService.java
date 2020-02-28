package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.service.GoogleDriveService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.bsuir.sdtt.util.GoogleProperty.APPLICATION_NAME;
import static com.bsuir.sdtt.util.GoogleProperty.CREDENTIALS_FILE_PATH;

/**
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Slf4j
@Service
public class DefaultGoogleDriveService implements GoogleDriveService {
    public static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static HttpTransport HTTP_TRANSPORT;
    private static GoogleClientSecrets clientSecrets;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private String authorizationCode;

    private String sourceUrl;

    private Credential credential;

    public DefaultGoogleDriveService() {
    }

    private InputStream getSecretFile() {
        return this.getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
    }

    private Drive getDriveService() {
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME).build();
    }

    @Override
    public GoogleClientSecrets getClientSecrets() {
        if (clientSecrets == null) {
            try {
                InputStreamReader clientSecretsReader = new InputStreamReader(getSecretFile());
                clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretsReader);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return clientSecrets;
    }

    @Override
    public Credential getCredentials() {
        return credential;
    }

    public boolean exchangeCode(String code) {
        this.authorizationCode = code;

        boolean result = false;
        String callbackUri = clientSecrets.getDetails().getRedirectUris().get(0);
        GoogleTokenResponse response;
        try {
            response = new GoogleAuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY,
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(), code, callbackUri).execute();

            credential = new GoogleCredential.Builder()
                    .setClientSecrets(clientSecrets)
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(HTTP_TRANSPORT)
                    .build()
                    .setAccessToken(response.getAccessToken())
                    .setRefreshToken(response.getRefreshToken());
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String getSourceUrl() {
        return sourceUrl;
    }

    @Override
    public String getRedirectUrl() {
        if (clientSecrets != null) {
            return clientSecrets.getDetails().getRedirectUris().get(0);
        }
        return null;
    }


    @Override
    public java.io.File convertStringToFile(String image) throws IOException {
        byte[] imageByte = Base64.decodeBase64(image);
        java.io.File convertedFile = new java.io.File(Objects.requireNonNull("sample.jpg"));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(imageByte);
        fos.close();
        return convertedFile;
    }

    @Override
    public String saveImageToGoogleDrive(java.io.File image) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(image.getName());
        FileContent mediaContent = new FileContent("image/jpeg", image);
        File file = getDriveService().files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        log.info("Save image to drive. File ID: {}", file.getId());

        return file.getId();
    }

    @Override
    public String compressionImage(java.io.File image) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(image);

        java.io.File compressedImageFile = new java.io.File("compress_" + image.getName());
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.1f);
        writer.write(null, new IIOImage(bufferedImage, null, null), param);

        os.close();
        ios.close();
        writer.dispose();
        return compressedImageFile.getAbsolutePath();
    }

    @Override
    public void deleteImageFromGoogleDrive(String imageId, String compressImageId) throws IOException {

        if (imageId != null && compressImageId != null && !imageId.isEmpty() && !compressImageId.isEmpty()) {
            getDriveService().files().delete(imageId).execute();
            getDriveService().files().delete(compressImageId).execute();
        }
    }
}