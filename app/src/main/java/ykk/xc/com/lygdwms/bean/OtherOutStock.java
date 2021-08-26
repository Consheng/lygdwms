package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

/**
 * 其他出库主表 ( t_OtherOutStock )
 */
public class OtherOutStock  implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String pdaNo;				// 单据号
	private String fdate;				// 出库日期
	private char type;					// 类型（A:报废，B:盘亏，C:返工发料，D:其他领用）
	private String remark;				// 备注
	private int createUserId;			// 创建用户id
	private String createUserName;		// 创建用户名称
	private String createDate;			// 创建日期
    
	public OtherOutStock() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPdaNo() {
		return pdaNo;
	}

	public void setPdaNo(String pdaNo) {
		this.pdaNo = pdaNo;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	
}
