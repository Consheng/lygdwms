package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

/**
 * 条码表
 * @author Administrator
 *
 */
public class BarcodeTable implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String snCode;					// 序列号
	private String batchCode;				// 批次号
	/**
	 * 1：仓库
	 * 2：库区
	 * 3：货架
	 * 4：库位
	 * 20：物料
	 * 30：生产入库单
	 * 31：采购入库单
	 * 32：其他入库单
	 * 33：直接调拨单
	 * 34：销售订单
	 */
	private int caseId;
	private int relationBillId;				// 关联单据id
	private int relationBillEntryId;		// 关联分录id
	private String relationBillNumber;		// 关联单据号
	private double relationBillQty;			// 关联单据数量
	private int forderBillId;				// 订单id
	private int forderEntryId;				// 订单分录id
	private String forderBillNo;			// 订单号
	private int materialId;					// 项目id
	private String materialNumber;			// 项目代码
	private String materialName;			// 项目名称
	private String materialSize;			// 项目规格
	private String barcode;					// 条码号
	private double barcodeQty;				// 条码数
	private double remainQty;				// 结存数量（发货后会减去对应数量，默认和条码数一致）
	private int printCount;					// 打印次数
	private String custNumber;				// 客户代码
	private String custName;				// 客户名称
	private int stockId;					// 仓库id
	private int stockAreaId;				// 库区id
	private int storageRackId;				// 货架id
	private int stockPositionId;			// 库位id
	private int createUserId;			// 创建用户id
	private String createUserName;		// 创建用户名称
	private String createDate;			// 创建日期
    
    private Stock stock;
    private StockArea stockArea;
    private StorageRack storageRack;
    private StockPosition stockPosition;

	// 临时字段，不存表
	private boolean check;			// 是否选中
	private String unitName;		// 单位名称
	private double smFqty;			// 扫描数

	public BarcodeTable() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSnCode() {
		return snCode;
	}

	public void setSnCode(String snCode) {
		this.snCode = snCode;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public int getCaseId() {
		return caseId;
	}

	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}

	public int getRelationBillId() {
		return relationBillId;
	}

	public void setRelationBillId(int relationBillId) {
		this.relationBillId = relationBillId;
	}

	public int getRelationBillEntryId() {
		return relationBillEntryId;
	}

	public void setRelationBillEntryId(int relationBillEntryId) {
		this.relationBillEntryId = relationBillEntryId;
	}

	public String getRelationBillNumber() {
		return relationBillNumber;
	}

	public void setRelationBillNumber(String relationBillNumber) {
		this.relationBillNumber = relationBillNumber;
	}

	public double getRelationBillQty() {
		return relationBillQty;
	}

	public void setRelationBillQty(double relationBillQty) {
		this.relationBillQty = relationBillQty;
	}

	public int getForderBillId() {
		return forderBillId;
	}

	public void setForderBillId(int forderBillId) {
		this.forderBillId = forderBillId;
	}

	public int getForderEntryId() {
		return forderEntryId;
	}

	public void setForderEntryId(int forderEntryId) {
		this.forderEntryId = forderEntryId;
	}

	public String getForderBillNo() {
		return forderBillNo;
	}

	public void setForderBillNo(String forderBillNo) {
		this.forderBillNo = forderBillNo;
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

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialSize() {
		return materialSize;
	}

	public void setMaterialSize(String materialSize) {
		this.materialSize = materialSize;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getBarcodeQty() {
		return barcodeQty;
	}

	public void setBarcodeQty(double barcodeQty) {
		this.barcodeQty = barcodeQty;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
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

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getStockAreaId() {
		return stockAreaId;
	}

	public void setStockAreaId(int stockAreaId) {
		this.stockAreaId = stockAreaId;
	}

	public int getStorageRackId() {
		return storageRackId;
	}

	public void setStorageRackId(int storageRackId) {
		this.storageRackId = storageRackId;
	}

	public int getStockPositionId() {
		return stockPositionId;
	}

	public void setStockPositionId(int stockPositionId) {
		this.stockPositionId = stockPositionId;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public StockArea getStockArea() {
		return stockArea;
	}

	public void setStockArea(StockArea stockArea) {
		this.stockArea = stockArea;
	}

	public StorageRack getStorageRack() {
		return storageRack;
	}

	public void setStorageRack(StorageRack storageRack) {
		this.storageRack = storageRack;
	}

	public StockPosition getStockPosition() {
		return stockPosition;
	}

	public void setStockPosition(StockPosition stockPosition) {
		this.stockPosition = stockPosition;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public double getRemainQty() {
		return remainQty;
	}

	public void setRemainQty(double remainQty) {
		this.remainQty = remainQty;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public double getSmFqty() {
		return smFqty;
	}

	public void setSmFqty(double smFqty) {
		this.smFqty = smFqty;
	}

}
