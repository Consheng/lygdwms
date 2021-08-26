package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * 单位表
 * @author Administrator
 *
 */
public class Unit_K3 implements Serializable {
	private static final long serialVersionUID = 1L;

	/*单位内码*/
	private int funitId;
	/*分配内码*/
	private String fmasterId;
	/*单位编码*/
	private String fnumber;
	/*单位名称*/
	private String fname;
	
	public Unit_K3() {
		super();
	}

	public int getFunitId() {
		return funitId;
	}

	public void setFunitId(int funitId) {
		this.funitId = funitId;
	}

	public String getFmasterId() {
		return fmasterId;
	}

	public void setFmasterId(String fmasterId) {
		this.fmasterId = fmasterId;
	}

	public String getFnumber() {
		return fnumber;
	}

	public void setFnumber(String fnumber) {
		this.fnumber = fnumber;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}
	
	
}
