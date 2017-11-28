package com.ylp.common.entity;

public class SystemUrl {
    private Integer id;

    private Integer sysBizId;

    private String url;

    private String cnName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysBizId() {
        return sysBizId;
    }

    public void setSysBizId(Integer sysBizId) {
        this.sysBizId = sysBizId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName == null ? null : cnName.trim();
    }
}