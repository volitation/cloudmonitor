package com.ylp.common.entity;

import java.io.Serializable;

public class LogLevel implements Serializable {
	private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String name;

    private Integer level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

	@Override
	public String toString() {
		return "LogLevel [id=" + id + ", code=" + code + ", name=" + name
				+ ", level=" + level + "]";
	}
    
    
    
}