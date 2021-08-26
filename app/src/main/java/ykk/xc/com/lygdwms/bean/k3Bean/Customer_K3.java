package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * @author King
 * @version 创建时间：2018年6月7日 下午2:55:52
 * @ClassName Customer
 * @Description 客户表
 */
public class Customer_K3 implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fpkId;			/*k3组织多语言表fpkid*/
	private int fcustId;		// K3部门id
	private String fnumber;		// K3部门编码
	private String fname;		// K3部门名称
	private int fuseOrgId;		// 使用组织id
	
	private Organization_K3 useOrg;
	
	// ��ʱ�ֶΣ������
    private boolean check;

	public Customer_K3() {
		super();
	}

	public int getFpkId() {
		return fpkId;
	}

	public void setFpkId(int fpkId) {
		this.fpkId = fpkId;
	}

	public int getFcustId() {
		return fcustId;
	}

	public void setFcustId(int fcustId) {
		this.fcustId = fcustId;
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

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	
}
