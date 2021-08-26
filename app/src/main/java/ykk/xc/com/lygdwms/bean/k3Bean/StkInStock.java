package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * 采购入库主表 ( t_Stk_instock )
 */
public class StkInStock implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int fid;
	private String fbillNo;
	private String fdate;
	
	public StkInStock() {
		super();
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public String getFbillNo() {
		return fbillNo;
	}

	public void setFbillNo(String fbillNo) {
		this.fbillNo = fbillNo;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	
	
	
}
