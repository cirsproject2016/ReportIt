package com.cirs.reportit.offline;

import com.cirs.reportit.utils.VolleyRequest.FileType;

import java.util.Arrays;

/**
 * Created by Rohan Kamat on 17-03-2016.
 */
public class EnqueuedImageRequest {
    private String url;
    private String method;
    private FileType fileType;
    private byte[] content;

    public EnqueuedImageRequest(String url, String method, FileType fileType, byte[] content) {
        this.url = url;
        this.method = method;
        this.fileType = fileType;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EnqueuedImageRequest{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", fileType=" + fileType +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
