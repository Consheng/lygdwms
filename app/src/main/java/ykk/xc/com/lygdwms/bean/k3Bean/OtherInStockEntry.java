package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * 其他入库分录 ( t_Stk_MiscellaneousEntry )
 */
public class OtherInStockEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fid;					// 主表id
	private int fentryId;				// 分录id
	private int fseq;					// 行号
	private int fmaterialId;			// 物料id
	private int funitId;				// 单位id
	private double fqty;				// 数量
	
	private OtherInStock otherInStock;
	private Material_K3 material;
	private Unit_K3 unit;
	
	public OtherInStockEntry() {
		super();
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public int getFentryId() {
		return fentryId;
	}

	public void setFentryId(int fentryId) {
		this.fentryId = fentryId;
	}

	public int getFmaterialId() {
		return fmaterialId;
	}

	public void setFmaterialId(int fmaterialId) {
		this.fmaterialId = fmaterialId;
	}

	public int getFunitId() {
		return funitId;
	}

	public void setFunitId(int funitId) {
		this.funitId = funitId;
	}

	public int getFseq() {
		return fseq;
	}

	public void setFseq(int fseq) {
		this.fseq = fseq;
	}

	public OtherInStock getOtherInStock() {
		return otherInStock;
	}

	public void setOtherInStock(OtherInStock otherInStock) {
		this.otherInStock = otherInStock;
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

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	
}
