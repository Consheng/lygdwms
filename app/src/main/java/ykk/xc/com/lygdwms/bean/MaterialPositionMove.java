package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3;

/**
 * 物料位置移动
 * @author Administrator
 *
 */
public class MaterialPositionMove implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int mtlId;						// 物料id
	private int barcodeTableId;				// 条码表id
	private String barcode;					// 条码号
	private int oldStockId;					// 旧仓库id
	private int oldStockAreaId;				// 旧库区id
	private int oldStorageRackId;			// 旧货架id
	private int oldStockPositionId;			// 旧库位id
	private int newStockId;					// 新仓库id
	private int newStockAreaId;				// 新库区id
	private int newStorageRackId;			// 新货架id
	private int newStockPositionId;			// 新库位id
	private int createUserId;				// 创建用户id
	private String createUserName;			// 创建用户名称
	private String createDate;				// 创建日期

	private Material_K3 material;
	private Stock oldStock;
	private StockArea oldStockArea;
	private StorageRack oldStorageRack;
	private StockPosition oldStockPosition;
	private Stock newStock;
	private StockArea newStockArea;
	private StorageRack newStorageRack;
	private StockPosition newStockPosition;

	// 临时字段，不存表
	private boolean check;			// 是否选中
	private String unitName;		// 单位名称

	public MaterialPositionMove() {
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

	public int getBarcodeTableId() {
		return barcodeTableId;
	}

	public void setBarcodeTableId(int barcodeTableId) {
		this.barcodeTableId = barcodeTableId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getOldStockId() {
		return oldStockId;
	}

	public void setOldStockId(int oldStockId) {
		this.oldStockId = oldStockId;
	}

	public int getOldStockAreaId() {
		return oldStockAreaId;
	}

	public void setOldStockAreaId(int oldStockAreaId) {
		this.oldStockAreaId = oldStockAreaId;
	}

	public int getOldStorageRackId() {
		return oldStorageRackId;
	}

	public void setOldStorageRackId(int oldStorageRackId) {
		this.oldStorageRackId = oldStorageRackId;
	}

	public int getOldStockPositionId() {
		return oldStockPositionId;
	}

	public void setOldStockPositionId(int oldStockPositionId) {
		this.oldStockPositionId = oldStockPositionId;
	}

	public int getNewStockId() {
		return newStockId;
	}

	public void setNewStockId(int newStockId) {
		this.newStockId = newStockId;
	}

	public int getNewStockAreaId() {
		return newStockAreaId;
	}

	public void setNewStockAreaId(int newStockAreaId) {
		this.newStockAreaId = newStockAreaId;
	}

	public int getNewStorageRackId() {
		return newStorageRackId;
	}

	public void setNewStorageRackId(int newStorageRackId) {
		this.newStorageRackId = newStorageRackId;
	}

	public int getNewStockPositionId() {
		return newStockPositionId;
	}

	public void setNewStockPositionId(int newStockPositionId) {
		this.newStockPositionId = newStockPositionId;
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
	
	public Stock getOldStock() {
		return oldStock;
	}

	public void setOldStock(Stock oldStock) {
		this.oldStock = oldStock;
	}

	public StockArea getOldStockArea() {
		return oldStockArea;
	}

	public void setOldStockArea(StockArea oldStockArea) {
		this.oldStockArea = oldStockArea;
	}

	public StorageRack getOldStorageRack() {
		return oldStorageRack;
	}

	public void setOldStorageRack(StorageRack oldStorageRack) {
		this.oldStorageRack = oldStorageRack;
	}

	public StockPosition getOldStockPosition() {
		return oldStockPosition;
	}

	public void setOldStockPosition(StockPosition oldStockPosition) {
		this.oldStockPosition = oldStockPosition;
	}

	public Stock getNewStock() {
		return newStock;
	}

	public void setNewStock(Stock newStock) {
		this.newStock = newStock;
	}

	public StockArea getNewStockArea() {
		return newStockArea;
	}

	public void setNewStockArea(StockArea newStockArea) {
		this.newStockArea = newStockArea;
	}

	public StorageRack getNewStorageRack() {
		return newStorageRack;
	}

	public void setNewStorageRack(StorageRack newStorageRack) {
		this.newStorageRack = newStorageRack;
	}

	public StockPosition getNewStockPosition() {
		return newStockPosition;
	}

	public void setNewStockPosition(StockPosition newStockPosition) {
		this.newStockPosition = newStockPosition;
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

	
}
