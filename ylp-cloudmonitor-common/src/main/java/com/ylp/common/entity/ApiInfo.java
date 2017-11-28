package com.ylp.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * api数据库映射对象
 * @author jack
 *
 */
public class ApiInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Long id;

    private Long sysBizId;

    private String name;

    private String cnName;

    private String url;
    
    private String system;
    
    private Date createTime;
    
    private Date modifyTime;

    public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSysBizId() {
        return sysBizId;
    }

    public void setSysBizId(Long sysBizId) {
        this.sysBizId = sysBizId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName == null ? null : cnName.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ApiInfo [id=" + id + ", sysBizId=" + sysBizId + ", name=" + name + ", cnName=" + cnName + ", url=" + url
				+ ", system=" + system + ", createTime=" + createTime + ", modifyTime=" + modifyTime + "]";
	}

	
  
	
    
}