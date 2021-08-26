package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

import ykk.xc.com.lygdwms.bean.k3Bean.Customer_K3;

/**
 * Wms 装车出库	主表
 * @author Administrator
 */
public class MaterialDelivery implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String pdaNo;				// 本地生产的流水号
	private int status;					// 单据业务状态 (0：创建，1：审核，2：关闭)
	private String fdate;				// 操作日期
	private int fcustId;				// 客户id
	private String deliveryNo;			// 发货单
	private String carNumber;			// 车牌号
	private int createUserId;			// 创建人id
	private String createUserName;		// 创建人
	private String createDate;			// 创建日期

	private Customer_K3 cust;
	
	public MaterialDelivery() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPdaNo() {
		return pdaNo;
	}

	public void setPdaNo(String pdaNo) {
		this.pdaNo = pdaNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getDeliveryNo() {
		return deliveryNo;
	}

	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
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

	public Customer_K3 getCust() {
		return cust;
	}

	public void setCust(Customer_K3 cust) {
		this.cust = cust;
	}

	
}
