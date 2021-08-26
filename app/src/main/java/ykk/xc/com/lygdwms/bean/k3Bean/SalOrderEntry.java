package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;
/**
 * 销售订单分录
 * @author Administrator
 *
 */
public class SalOrderEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fid; 								// 单据id,
	private int fentryId;							// 分录id
	private int fseq;								// 序号
	private int fmaterialId;						// 物料id
	private int funitId;							// 单位id
	private double fqty;							// 数量

	private SalOrder salOrder;
	private Material_K3 material;
	private Unit_K3 unit;

	// 临时字段，不存表
	private double usableQty; 			// 可用的数量
	
	public SalOrderEntry() {
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

	public int getFseq() {
		return fseq;
	}

	public void setFseq(int fseq) {
		this.fseq = fseq;
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

	public double getUsableQty() {
		return usableQty;
	}

	public void setUsableQty(double usableQty) {
		this.usableQty = usableQty;
	}

	
}
