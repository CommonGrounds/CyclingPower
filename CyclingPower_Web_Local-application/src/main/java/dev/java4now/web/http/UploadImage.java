package dev.java4now.web.http;

import dev.java4now.web.Settings;
import dev.java4now.web.model.futures_fetcher;
import dev.webfx.platform.blob.Blob;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.fetch.Fetch;
import dev.webfx.platform.fetch.FetchOptions;
import dev.webfx.platform.fetch.FormData;
import dev.webfx.platform.fetch.Headers;
import dev.webfx.platform.file.File;

public class UploadImage {


    public static void uploadBlob(String json_name, Blob imageBlob, String filename, int image_counter) {
        if (imageBlob == null || imageBlob.length() == 0) {
            Console.log("Invalid blob for upload.");
            return;
        }

        FormData formData = new FormData();
        formData.append("file", imageBlob, filename);  // Blob + filename works for multipart
        formData.append("jsonFile", json_name);

        FetchOptions options = new FetchOptions();
        options.setMethod("POST");
        options.setBody(formData);

        Headers headers = Headers.create();
        String auth = Settings.name_txt.get() + ":" + Settings.pass_txt.get();
        headers.set("Authorization", "Basic " + SimpleBase64.encode(auth));
        options.setHeaders(headers);

        Console.log("Uploading resized blob: " + filename + " (size: " + imageBlob.length() + " bytes) for JSON: " + json_name + " (counter: " + image_counter + ")");

        Fetch.fetch(futures_fetcher.IMAGE_UPLOAD_URL, options)
                .onSuccess(response -> {
                    Console.log("Response status: " + response.status());
                    if (response.status() == 200) {
                        response.text()
                                .onSuccess(text -> {
                                    Console.log("Upload success: " + text);
//                                    UpPane.fetchImagesForJson(json_name);  // Refetch to update UI
                                })
                                .onFailure(error -> Console.log("Failed to read response: " + error.getMessage()));
                    } else {
                        response.text()
                                .onFailure(error -> Console.log("Upload failed (status: " + response.status() + ")"))
                                .onSuccess(errorText -> Console.log("Upload failed: " + errorText));
                    }
                })
                .onFailure(error -> Console.log("Fetch error: " + error.getMessage()));
    }



    public static void upload(String json_name, File imageFile, int image_counter) {
        if (imageFile == null) {
            Console.log("No file selected for upload.");
            return;
        }

        Blob imageBlob = imageFile;
        if (imageBlob == null || imageBlob.length() == 0) {
            Console.log("Invalid blob: null or empty for file " + imageFile.getName());
            return;
        }

        FormData formData = new FormData();
        formData.append("file", imageBlob, imageFile.getName());  // Use getBlob() - standard WebFX Blob
        formData.append("jsonFile", json_name);

        FetchOptions options = new FetchOptions();
        options.setMethod("POST");
        options.setBody(formData);

        Headers headers = Headers.create();
        String auth = Settings.name_txt.get() + ":" + Settings.pass_txt.get();
        headers.set("Authorization", "Basic " + SimpleBase64.encode(auth));
        // Do NOT set Content-Type - let Fetch/FormData handle multipart boundary automatically
        options.setHeaders(headers);

        Console.log("Uploading image: " + imageFile.getName() + " (size: " + imageBlob.length() + " bytes, type: " + imageFile.getMimeType() + ") for JSON: " + json_name + " (counter: " + image_counter + ")");

        Fetch.fetch(futures_fetcher.IMAGE_UPLOAD_URL, options)
                .onSuccess(response -> {
                    Console.log("Response status: " + response.status());
                    if (response.status() == 200) {
                        response.text()
                                .onSuccess(text -> {
                                    Console.log("Upload success: " + text);
                                    // Refetch images to update UI after successful upload
//                                    UpPane.fetchImagesForJson(json_name);
                                })
                                .onFailure(error -> Console.log("Failed to read upload response: " + error.getMessage()));
                    } else {
                        response.text()
                                .onFailure(error -> Console.log("Upload failed with status: " + response.status() + " (no response body)"))
                                .onSuccess(errorText -> Console.log("Upload failed with status: " + response.status() + " - " + errorText));
                    }
                })
                .onFailure(error -> Console.log("Fetch error during upload: " + error.getMessage()));
    }
}