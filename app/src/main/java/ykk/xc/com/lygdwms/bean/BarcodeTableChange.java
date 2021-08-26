package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3;

/**
 * 条码拆解组装	记录
 * @author Administrator
 *
 */
public class BarcodeTableChange implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int mtlId;					// 物料id
	private int scanBarcodeTableId;		// 条码表id
	private String scanBarcode;			// 扫的条码
	private double scanQty;				// 扫的条码数
	private int newBarcodeTableId;		// 条码表id
	private String newBarcode;			// 新的条码
	private double newQty;				// 新的条码数
	private char type;					// 类型	A：组合，B：拆解
	private int createUserId;			// 创建用户id
	private String createUserName;		// 创建用户名称
	private String createDate;			// 创建日期

	private Material_K3 material;

	// 临时字段，不存表
	private String unitName;		// 单位名称

	public BarcodeTableChange() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public int getScanBarcodeTableId() {
		return scanBarcodeTableId;
	}

	public void setScanBarcodeTableId(int scanBarcodeTableId) {
		this.scanBarcodeTableId = scanBarcodeTableId;
	}

	public String getScanBarcode() {
		return scanBarcode;
	}

	public void setScanBarcode(String scanBarcode) {
		this.scanBarcode = scanBarcode;
	}

	public double getScanQty() {
		return scanQty;
	}

	public void setScanQty(double scanQty) {
		this.scanQty = scanQty;
	}

	public int getNewBarcodeTableId() {
		return newBarcodeTableId;
	}

	public void setNewBarcodeTableId(int newBarcodeTableId) {
		this.newBarcodeTableId = newBarcodeTableId;
	}

	public String getNewBarcode() {
		return newBarcode;
	}

	public void setNewBarcode(String newBarcode) {
		this.newBarcode = newBarcode;
	}

	public double getNewQty() {
		return newQty;
	}

	public void setNewQty(double newQty) {
		this.newQty = newQty;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
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

	public Material_K3 getMaterial() {
		return material;
	}

	public void setMaterial(Material_K3 material) {
		this.material = material;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}


}
