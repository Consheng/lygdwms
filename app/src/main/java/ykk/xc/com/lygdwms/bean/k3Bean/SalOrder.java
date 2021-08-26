package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;
/**
 * 销售订单
 * @author Administrator
 *
 */
public class SalOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fid; 						// 单据id
	private String fbillNo; 				// 单据编号
	private String fdate; 					// 日期
	private int fcustId; 					// 客户Id
	
	private Customer_K3 cust;
	
	public SalOrder() {
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

	public int getFcustId() {
		return fcustId;
	}

	public void setFcustId(int fcustId) {
		this.fcustId = fcustId;
	}

	public Customer_K3 getCust() {
		return cust;
	}

	public void setCust(Customer_K3 cust) {
		this.cust = cust;
	}

}
