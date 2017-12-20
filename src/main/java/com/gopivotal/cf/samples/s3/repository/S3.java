package com.gopivotal.cf.samples.s3.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Collectors;

public class S3 {

    private AmazonS3 amazonS3;
    private String bucket;
    private String baseUrl;

    public S3(AmazonS3 amazonS3, String bucket, String baseUrl) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.baseUrl = baseUrl;
    }

    public S3File createS3FileObject(String id, String name, File file) {
        return new S3File(id, bucket, name, file);
    }

    public URL put(S3File file) throws MalformedURLException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, file.getActualFileName(), file.getFile())
                .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(putObjectRequest);
        URL url;
        if (baseUrl == null) {
            url = amazonS3.getUrl(bucket, file.getActualFileName());
        } else {
            url = new URL(baseUrl + "/" + bucket + "/" + file.getActualFileName());
        }
        return url;
    }

    public void delete(S3File file) {
        amazonS3.deleteObject(bucket, file.getActualFileName());
    }


    public Iterable<S3File> getAll() {
        ObjectListing objectListing = amazonS3.listObjects(bucket);

        return objectListing.getObjectSummaries().stream().map(s3ObjectSummary -> {
            S3Object object = amazonS3.getObject(bucket, s3ObjectSummary.getKey());

            Path temp = null;
            try {
                temp = Files.createTempFile("/tmp/", "temp");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream outputStream = new FileOutputStream(temp.toFile());
                int read = 0;
                byte[] bytes = new byte[1024];

                InputStream inputStream = object.getObjectContent();

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            S3File s3File = new S3File(object.getKey(), bucket, object.getKey(), temp.toFile());
            URL url = amazonS3.getUrl(bucket, object.getKey());
            s3File.setUrl(url);
            return s3File;
        }).collect(Collectors.toList());
    }
}