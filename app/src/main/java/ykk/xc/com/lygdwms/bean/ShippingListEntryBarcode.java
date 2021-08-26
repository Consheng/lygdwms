package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

/**
 * 出货清单扫码出库记录
 * @author Administrator
 *
 */
public class ShippingListEntryBarcode implements Serializable {
	private static final long serialVersionUID = 1L;

	//自增id
	private int id;

	//shippingListEntry自增id
	private int parentId;

	private ShippingListEntry entry;

	//条码表id
	private int barcodeId;

	private BarcodeTable barcodeTable;

	//条码数量
	private double barcodeQty;

	//实际出库数量
	private double fqty;

	//操作人id
	private int createUserId;

	//操作人名称
	private String createUserName;

	//操作时间
	private String createTime;

	public ShippingListEntryBarcode() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getBarcodeId() {
		return barcodeId;
	}

	public void setBarcodeId(int barcodeId) {
		this.barcodeId = barcodeId;
	}

	public double getBarcodeQty() {
		return barcodeQty;
	}

	public void setBarcodeQty(double barcodeQty) {
		this.barcodeQty = barcodeQty;
	}

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public ShippingListEntry getEntry() {
		return entry;
	}

	public void setEntry(ShippingListEntry entry) {
		this.entry = entry;
	}

	public BarcodeTable getBarcodeTable() {
		return barcodeTable;
	}

	public void setBarcodeTable(BarcodeTable barcodeTable) {
		this.barcodeTable = barcodeTable;
	}

	@Override
	public String toString() {
		return "ShippingListEntryBarcode [id=" + id + ", parentId=" + parentId + ", entry=" + entry + ", barcodeId="
				+ barcodeId + ", barcodeTable=" + barcodeTable + ", barcodeQty=" + barcodeQty + ", fqty=" + fqty
				+ ", createUserId=" + createUserId + ", createUserName=" + createUserName + ", createTime=" + createTime
				+ "]";
	}

}
