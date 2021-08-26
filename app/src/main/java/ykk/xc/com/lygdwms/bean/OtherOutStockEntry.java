package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3;

/**
 * 其他出库分录表 ( t_OtherOutStockEntry )
 */
public class OtherOutStockEntry  implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int parentId;				// 主表id
	private int barcodeTableId;			// 条码表id
	private String barcode;				// 条码号
	private int mtlId;					// 物料id
	private double sourceQty;			// 源单数量
	private double fqty;				// 数量
	
	private OtherOutStock otherOutStock;
	private BarcodeTable bt;
	private Material_K3 material;
    
	public OtherOutStockEntry() {
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

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public double getSourceQty() {
		return sourceQty;
	}

	public void setSourceQty(double sourceQty) {
		this.sourceQty = sourceQty;
	}

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public OtherOutStock getOtherOutStock() {
		return otherOutStock;
	}

	public void setOtherOutStock(OtherOutStock otherOutStock) {
		this.otherOutStock = otherOutStock;
	}

	public BarcodeTable getBt() {
		return bt;
	}

	public void setBt(BarcodeTable bt) {
		this.bt = bt;
	}

	public Material_K3 getMaterial() {
		return material;
	}

	public void setMaterial(Material_K3 material) {
		this.material = material;
	}

	
	
}
