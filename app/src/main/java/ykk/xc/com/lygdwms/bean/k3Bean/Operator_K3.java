package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * @Description 业务员表
 */
public class Operator_K3 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fnumber;			// 业务员代码
	private String fname;			// 业务员名称
	private String foperatorType;	// 业务员类型 （ 销售员：XSY，采购员：CGY，仓管员：WHY，计划员：JHY ）
	private int fuseOrgId;			// 使用组织id
	private String fmobile;			// 员工手机号

	private Organization_K3 useOrg;
	
	public Operator_K3() {
		super();
	}

	public String getFnumber() {
		return fnumber;
	}

	public void setFnumber(String fnumber) {
		this.fnumber = fnumber;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getFoperatorType() {
		return foperatorType;
	}

	public void setFoperatorType(String foperatorType) {
		this.foperatorType = foperatorType;
	}

	public int getFuseOrgId() {
		return fuseOrgId;
	}

	public void setFuseOrgId(int fuseOrgId) {
		this.fuseOrgId = fuseOrgId;
	}

	public Organization_K3 getUseOrg() {
		return useOrg;
	}

	public void setUseOrg(Organization_K3 useOrg) {
		this.useOrg = useOrg;
	}

	public String getFmobile() {
		return fmobile;
	}

	public void setFmobile(String fmobile) {
		this.fmobile = fmobile;
	}

	
	
}
