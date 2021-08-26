package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * 部门信息 t_Department
 */
public class Department_K3 implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fpkId;			// k3组织多语言表fpkid
	private int fdeptId;		// K3部门id
	private String fnumber;		// K3部门编码
	private String fname;		// K3部门名称
	private int fuseOrgId;		// K3部门使用组织id
	private int fwipStockId;	// WIP仓库id
	private int fwipLocationId;	// WIP仓位id
	
	private Organization_K3 useOrg;

	// 临时字段，不存表
    private boolean check;

	public Department_K3() {
		super();
	}

	public int getFpkId() {
		return fpkId;
	}

	public void setFpkId(int fpkId) {
		this.fpkId = fpkId;
	}

	public int getFdeptId() {
		return fdeptId;
	}

	public void setFdeptId(int fdeptId) {
		this.fdeptId = fdeptId;
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

	public int getFwipStockId() {
		return fwipStockId;
	}

	public void setFwipStockId(int fwipStockId) {
		this.fwipStockId = fwipStockId;
	}

	public int getFwipLocationId() {
		return fwipLocationId;
	}

	public void setFwipLocationId(int fwipLocationId) {
		this.fwipLocationId = fwipLocationId;
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
