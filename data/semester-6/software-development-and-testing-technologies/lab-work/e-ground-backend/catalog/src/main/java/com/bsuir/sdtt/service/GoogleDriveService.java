package com.bsuir.sdtt.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Interface of google api image service.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
public interface GoogleDriveService {
    File convertStringToFile(String image) throws IOException;

    String saveImageToGoogleDrive(java.io.File image) throws GeneralSecurityException, IOException;

    String compressionImage(java.io.File image) throws IOException, GeneralSecurityException;

    void deleteImageFromGoogleDrive(String imageId, String compressImageId) throws GeneralSecurityException, IOException;

    GoogleClientSecrets getClientSecrets();

    Credential getCredentials();

    String getSourceUrl();

    String getRedirectUrl();

}