package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;

/**
 * 组织表
 *
 */
public class Organization_K3 implements Serializable {
	private static final long serialVersionUID = 1L;

	/*k3组织多语言表fpkid*/
	private int fpkId;
	/*k3组织id*/
	private int forgId;
	/*组织编码*/
	private String fnumber;
	/*组织名称*/
	private String fname;

	public Organization_K3() {
		super();
	}

	public int getFpkId() {
		return fpkId;
	}

	public void setFpkId(int fpkId) {
		this.fpkId = fpkId;
	}

	public int getForgId() {
		return forgId;
	}

	public void setForgId(int forgId) {
		this.forgId = forgId;
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
