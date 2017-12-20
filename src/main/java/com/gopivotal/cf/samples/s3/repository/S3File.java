package com.gopivotal.cf.samples.s3.repository;

import java.io.File;
import java.net.URL;

public class S3File {

    private String id;
    private String bucket;
    private String name;
    private URL url;
    private File file;

    public S3File() {
    }

   public S3File(String id, String bucket, String name, File file) {
        this.id = id;
        this.bucket = bucket;
        this.name = name;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getBucket() {
        return bucket;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public String getActualFileName() {
        return id + "/" + name;
    }

}