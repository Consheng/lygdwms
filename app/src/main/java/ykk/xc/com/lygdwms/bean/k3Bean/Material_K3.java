package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 *  物料表
 */
public class Material_K3 implements Serializable {
	private static final long serialVersionUID = 1L;

	/* k3物料id */
	private int fmaterialId;
	/* k3物料编号 */
	private String fnumber;
	/* k3物料名称 */
	private String fname;
	/* 使用组织ID */
	private String fuseOrgId;
	/* 基本单位id */
	private int funitId;
	/* 是否启用批号管理，0代表不启用，1代表启用 */
	private int isBatchManager;
	/* 是否启用序列号管理，0代表不启用，1代表启用 */
	private int isSnManager;
	/* 产品规格 */
	private String materialSize;
	/* k3旧物料编码 */
	private String oldNumber;
	/* k3旧物料名称 */
	private String oldName;
	/* 备注 */
	private String remarks;

	private Organization_K3 useOrg;
	private Unit_K3 unit;

	// 临时字段，不存表
	private boolean check; // 是否选中
	private double barcodeQty; // 条码数量
	
	public Material_K3() {
		super();
	}

	public int getFmaterialId() {
		return fmaterialId;
	}

	public void setFmaterialId(int fmaterialId) {
		this.fmaterialId = fmaterialId;
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

	public String getFuseOrgId() {
		return fuseOrgId;
	}

	public void setFuseOrgId(String fuseOrgId) {
		this.fuseOrgId = fuseOrgId;
	}

	public int getFunitId() {
		return funitId;
	}

	public void setFunitId(int funitId) {
		this.funitId = funitId;
	}

	public int getIsBatchManager() {
		return isBatchManager;
	}

	public void setIsBatchManager(int isBatchManager) {
		this.isBatchManager = isBatchManager;
	}

	public int getIsSnManager() {
		return isSnManager;
	}

	public void setIsSnManager(int isSnManager) {
		this.isSnManager = isSnManager;
	}

	public String getMaterialSize() {
		return materialSize;
	}

	public void setMaterialSize(String materialSize) {
		this.materialSize = materialSize;
	}

	public String getOldNumber() {
		return oldNumber;
	}

	public void setOldNumber(String oldNumber) {
		this.oldNumber = oldNumber;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Organization_K3 getUseOrg() {
		return useOrg;
	}

	public void setUseOrg(Organization_K3 useOrg) {
		this.useOrg = useOrg;
	}

	public Unit_K3 getUnit() {
		return unit;
	}

	public void setUnit(Unit_K3 unit) {
		this.unit = unit;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public double getBarcodeQty() {
		return barcodeQty;
	}

	public void setBarcodeQty(double barcodeQty) {
		this.barcodeQty = barcodeQty;
	}

	
	
}
