package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * 生产入库分录 ( t_Prd_instockEntry )
 */
public class PrdInStockEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fid;					// 主表id
	private int fentryId;				// 分录id
	private int fseq;					// 行号
	private int fmaterialId;			// 物料id
	private int funitId;				// 单位id
	private double fqty;				// 数量
	private int freqBillId;				// 需求单据内码id
	private int freqEntryId;			// 需求单据分录id
	private String freqBillNo;			// 需求单据号
	
	private PrdInStock prdInStock;
	private Material_K3 material;
	private Unit_K3 unit;
	private SalOrder salOrder;
	
	public PrdInStockEntry() {
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

	public PrdInStock getPrdInStock() {
		return prdInStock;
	}

	public void setPrdInStock(PrdInStock prdInStock) {
		this.prdInStock = prdInStock;
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

	public SalOrder getSalOrder() {
		return salOrder;
	}

	public void setSalOrder(SalOrder salOrder) {
		this.salOrder = salOrder;
	}

	public int getFreqBillId() {
		return freqBillId;
	}

	public void setFreqBillId(int freqBillId) {
		this.freqBillId = freqBillId;
	}

	public int getFreqEntryId() {
		return freqEntryId;
	}

	public void setFreqEntryId(int freqEntryId) {
		this.freqEntryId = freqEntryId;
	}

	public String getFreqBillNo() {
		return freqBillNo;
	}

	public void setFreqBillNo(String freqBillNo) {
		this.freqBillNo = freqBillNo;
	}
	
}
