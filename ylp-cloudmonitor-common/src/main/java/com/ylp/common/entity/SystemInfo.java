package com.ylp.common.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统信息数据库映射对象
 * @author jack
 *
 */
public class SystemInfo  implements Serializable{

	private static final long serialVersionUID = 1L;

    private Long id;

	private List<ApiInfo> subset;

	private String name;

    private String cnName;

    private Date createTime;

    private Integer deleted;

    private Integer sysBizId;
   
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getSysBizId() {
        return sysBizId;
    }

    public void setSysBizId(Integer sysBizId) {
        this.sysBizId = sysBizId;
    }
  
    

	public List<ApiInfo> getSubset() {
		return subset;
	}

	
	public void setSubset(List<ApiInfo> subset) {
		this.subset = subset;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SystemInfo [id=" + id + ", subset=" + subset + ", name=" + name + ", cnName=" + cnName
				+ ", createTime=" + createTime + ", deleted=" + deleted + ", sysBizId=" + sysBizId + "]";
	}

	
	
    
    
}