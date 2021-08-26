package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 出货清单子表实体类
 * @author Administrator
 *
 */
public class ShippingListEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	//id
	private int id;
	//主表
	private int fid;
	//订单号
	private String orderNo;
	//订单id
	private int orderId;
	//客户编码
	private String customerNo;
	//客户id
	private int customerId;
	//行号
	private int lineNumber;
	//订单行id
	private int orderEntryId;
	//订单行物料id
	private int materialId;
	//订单行物料编码
	private String materialNumber;
	//品名
	private String productName;
	//型号
	private String model;
	//数量
	private double qty;
	//出货数量
	private double outQty;
	//计量单位名称
	private String unitName;
	//箱数
	private int boxesNumber;
	//净重
	private double netWeight;
	//出货箱数
	private int outBoxesNumber;
	//毛重
	private double hairWeight;
	//过磅重量
	private double overWeight;
	//品牌
	private String brand;
	//功率/用途
	private String powerAndUse;
	//外箱尺寸1
	private double outerBoxSize1;
	//外箱尺寸2
	private double outerBoxSize2;
	//外箱尺寸3
	private double outerBoxSize3;
	//CBM
	private double cbm;
	//备注
	private String remark;


	//以下字段不存表，临时使用
	//仓库id
	private int stockId;
	//仓库
	private Stock stock;
	//库区id
	private int stockAreaId;
	//库区
	private StockArea stockArea;
	//货架id
	private int storageRackId;
	//货架
	private StorageRack storageRack;
	//库位id
	private int stockPositionId;
	//库位
	private StockPosition stockPostion;
	//条码数量
	private double barcodeQty;
	// 可用数量
	private double usableQty;

	private List<ShippingListEntryBarcode> shippingListEntryBarcodes;

	public ShippingListEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getOrderEntryId() {
		return orderEntryId;
	}

	public void setOrderEntryId(int orderEntryId) {
		this.orderEntryId = orderEntryId;
	}

	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public double getOutQty() {
		return outQty;
	}

	public void setOutQty(double outQty) {
		this.outQty = outQty;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public int getBoxesNumber() {
		return boxesNumber;
	}

	public void setBoxesNumber(int boxesNumber) {
		this.boxesNumber = boxesNumber;
	}

	public double getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(double netWeight) {
		this.netWeight = netWeight;
	}

	public int getOutBoxesNumber() {
		return outBoxesNumber;
	}

	public void setOutBoxesNumber(int outBoxesNumber) {
		this.outBoxesNumber = outBoxesNumber;
	}

	public double getHairWeight() {
		return hairWeight;
	}

	public void setHairWeight(double hairWeight) {
		this.hairWeight = hairWeight;
	}

	public double getOverWeight() {
		return overWeight;
	}

	public void setOverWeight(double overWeight) {
		this.overWeight = overWeight;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getPowerAndUse() {
		return powerAndUse;
	}

	public void setPowerAndUse(String powerAndUse) {
		this.powerAndUse = powerAndUse;
	}

	public double getOuterBoxSize1() {
		return outerBoxSize1;
	}

	public void setOuterBoxSize1(double outerBoxSize1) {
		this.outerBoxSize1 = outerBoxSize1;
	}

	public double getOuterBoxSize2() {
		return outerBoxSize2;
	}

	public void setOuterBoxSize2(double outerBoxSize2) {
		this.outerBoxSize2 = outerBoxSize2;
	}

	public double getOuterBoxSize3() {
		return outerBoxSize3;
	}

	public void setOuterBoxSize3(double outerBoxSize3) {
		this.outerBoxSize3 = outerBoxSize3;
	}

	public double getCbm() {
		return cbm;
	}

	public void setCbm(double cbm) {
		this.cbm = cbm;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public int getStockAreaId() {
		return stockAreaId;
	}

	public void setStockAreaId(int stockAreaId) {
		this.stockAreaId = stockAreaId;
	}

	public StockArea getStockArea() {
		return stockArea;
	}

	public void setStockArea(StockArea stockArea) {
		this.stockArea = stockArea;
	}

	public int getStorageRackId() {
		return storageRackId;
	}

	public void setStorageRackId(int storageRackId) {
		this.storageRackId = storageRackId;
	}

	public StorageRack getStorageRack() {
		return storageRack;
	}

	public void setStorageRack(StorageRack storageRack) {
		this.storageRack = storageRack;
	}

	public int getStockPositionId() {
		return stockPositionId;
	}

	public void setStockPositionId(int stockPositionId) {
		this.stockPositionId = stockPositionId;
	}

	public StockPosition getStockPostion() {
		return stockPostion;
	}

	public void setStockPostion(StockPosition stockPostion) {
		this.stockPostion = stockPostion;
	}

	public double getBarcodeQty() {
		return barcodeQty;
	}

	public void setBarcodeQty(double barcodeQty) {
		this.barcodeQty = barcodeQty;
	}

	public double getUsableQty() {
		return usableQty;
	}

	public void setUsableQty(double usableQty) {
		this.usableQty = usableQty;
	}

	public List<ShippingListEntryBarcode> getShippingListEntryBarcodes() {
		if(shippingListEntryBarcodes == null) {
			shippingListEntryBarcodes = new ArrayList<>();
		}
		return shippingListEntryBarcodes;
	}

	public void setShippingListEntryBarcodes(List<ShippingListEntryBarcode> shippingListEntryBarcodes) {
		this.shippingListEntryBarcodes = shippingListEntryBarcodes;
	}

	@Override
	public String toString() {
		return "ShippingListEntry [id=" + id + ", fid=" + fid + ", orderNo=" + orderNo + ", orderId=" + orderId
				+ ", customerNo=" + customerNo + ", customerId=" + customerId + ", lineNumber=" + lineNumber + ", orderEntryId="
				+ orderEntryId + ", materialId=" + materialId + ", materialNumber=" + materialNumber + ", productName="
				+ productName + ", model=" + model + ", qty=" + qty + ", outQty=" + outQty + ", unitName=" + unitName
				+ ", boxesNumber=" + boxesNumber + ", netWeight=" + netWeight + ", outBoxesNumber=" + outBoxesNumber
				+ ", hairWeight=" + hairWeight + ", overWeight=" + overWeight + ", brand=" + brand + ", powerAndUse="
				+ powerAndUse + ", outerBoxSize1=" + outerBoxSize1 + ", outerBoxSize2=" + outerBoxSize2
				+ ", outerBoxSize3=" + outerBoxSize3 + ", cbm=" + cbm + ", remark=" + remark + ", stockId=" + stockId
				+ ", stock=" + stock + ", stockAreaId=" + stockAreaId + ", stockArea=" + stockArea + ", storageRackId="
				+ storageRackId + ", storageRack=" + storageRack + ", stockPositionId=" + stockPositionId
				+ ", stockPostion=" + stockPostion + ", barcodeQty=" + barcodeQty + "]";
	}

}
