package com.cirs.reportit.offline;

/**
 * Created by Rohan Kamat on 16-03-2016.
 */
public class EnqueuedRequest<T> {
    private int method;
    private String url;

    public EnqueuedRequest(int method, String url, Object requestBody, Class<T> clazz) {
        this.method = method;
        this.url = url;
        this.requestBody = requestBody;
        this.clazz = clazz;
    }

    private Object requestBody;
    private Class<T> clazz;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "EnqueuedRequest{" +
                "method=" + method +
                ", url='" + url + '\'' +
                ", requestBody=" + requestBody +
                ", clazz=" + clazz +
                '}';
    }
}
