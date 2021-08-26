package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3;
import ykk.xc.com.lygdwms.bean.k3Bean.Unit_K3;

/**
 * Wms 本地的出入库	Entry表
 * @author Administrator
 *
 */
public class MaterialDeliveryEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id; 					//
	private int materialDeliveryId;		// 主表id
	private int fsourceInterId;			// 来源内码id
	private int fsourceEntryId;			// 来源分录id
	private String fsourceBillNo;		// 来源单号
	private double fsourceQty;			// 来源单数量
	private double usableQty;			// 当前发货数
	private int mtlId;					// 物料id
	private double fqty;				// 数量
	private double fprice;				// 单价
	private int funitId;				// 单位id
	private int stockId; 				// WMS 仓库id
	private int stockAreaId; 			// WMS 库区id
	private int storageRackId; 			// WMS 货架id
	private int stockPositionId; 		// WMS 库位id
	
	private MaterialDelivery materialDelivery;
	private Stock stock;
	private StockArea stockArea;
	private StorageRack storageRack;
	private StockPosition stockPosition;
	private Material_K3 material;
	private Unit_K3 unit;
	
	public MaterialDeliveryEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMaterialDeliveryId() {
		return materialDeliveryId;
	}

	public void setMaterialDeliveryId(int materialDeliveryId) {
		this.materialDeliveryId = materialDeliveryId;
	}

	public int getFsourceInterId() {
		return fsourceInterId;
	}

	public void setFsourceInterId(int fsourceInterId) {
		this.fsourceInterId = fsourceInterId;
	}

	public int getFsourceEntryId() {
		return fsourceEntryId;
	}

	public void setFsourceEntryId(int fsourceEntryId) {
		this.fsourceEntryId = fsourceEntryId;
	}

	public String getFsourceBillNo() {
		return fsourceBillNo;
	}

	public void setFsourceBillNo(String fsourceBillNo) {
		this.fsourceBillNo = fsourceBillNo;
	}

	public double getFsourceQty() {
		return fsourceQty;
	}

	public void setFsourceQty(double fsourceQty) {
		this.fsourceQty = fsourceQty;
	}

	public double getUsableQty() {
		return usableQty;
	}

	public void setUsableQty(double usableQty) {
		this.usableQty = usableQty;
	}

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public double getFprice() {
		return fprice;
	}

	public void setFprice(double fprice) {
		this.fprice = fprice;
	}

	public int getFunitId() {
		return funitId;
	}

	public void setFunitId(int funitId) {
		this.funitId = funitId;
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

	public MaterialDelivery getMaterialDelivery() {
		return materialDelivery;
	}

	public void setMaterialDelivery(MaterialDelivery materialDelivery) {
		this.materialDelivery = materialDelivery;
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

	public Material_K3 getMaterial() {
		return material;
	}

	public void setMaterial(Material_K3 material) {
		this.material = material;
	}

	public Unit_K3 getUnit() {
		return unit;
	}

	public void setUnit(Unit_K3 unit) {
		this.unit = unit;
	}

	
	
}
