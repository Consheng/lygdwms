package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

/**
 * 出货清单主表实体类
 * @author Administrator
 *
 */
public class ShippingList implements Serializable {
	private static final long serialVersionUID = 1L;

	//id
	private int id;
	//标题
	private String title;
	//装柜要求
	private String requirements;
	//SO
	private String so;
	//柜号
	private String cabinetNo;
	//封条
	private String seal;
	//车牌
	private String carLicense;
	//要求货柜到厂时间
	private String needArrivalTime;
	//货柜实际到厂时间
	private String actualArrivalTime;
	//货柜离厂时间
	private String leaveTime;
	//延时原因
	private String delayReason;

	//状态 1未出货，2已出货
	private int status;
	//导入时间
	private String loadTime;
	//导入人
	private int loadUserId;
	private User loadUser;
	//出货时间
	private String shippingOutTime;
	//出货操作人
	private int shippingOutUserId;
	private User shippingOutUser;
	
	public ShippingList() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public String getSo() {
		return so;
	}

	public void setSo(String so) {
		this.so = so;
	}

	public String getCabinetNo() {
		return cabinetNo;
	}

	public void setCabinetNo(String cabinetNo) {
		this.cabinetNo = cabinetNo;
	}

	public String getSeal() {
		return seal;
	}

	public void setSeal(String seal) {
		this.seal = seal;
	}

	public String getCarLicense() {
		return carLicense;
	}

	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}

	public String getNeedArrivalTime() {
		return needArrivalTime;
	}

	public void setNeedArrivalTime(String needArrivalTime) {
		this.needArrivalTime = needArrivalTime;
	}

	public String getActualArrivalTime() {
		return actualArrivalTime;
	}

	public void setActualArrivalTime(String actualArrivalTime) {
		this.actualArrivalTime = actualArrivalTime;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getDelayReason() {
		return delayReason;
	}

	public void setDelayReason(String delayReason) {
		this.delayReason = delayReason;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(String loadTime) {
		this.loadTime = loadTime;
	}

	public int getLoadUserId() {
		return loadUserId;
	}

	public void setLoadUserId(int loadUserId) {
		this.loadUserId = loadUserId;
	}

	public User getLoadUser() {
		return loadUser;
	}

	public void setLoadUser(User loadUser) {
		this.loadUser = loadUser;
	}

	public String getShippingOutTime() {
		return shippingOutTime;
	}

	public void setShippingOutTime(String shippingOutTime) {
		this.shippingOutTime = shippingOutTime;
	}

	public int getShippingOutUserId() {
		return shippingOutUserId;
	}

	public void setShippingOutUserId(int shippingOutUserId) {
		this.shippingOutUserId = shippingOutUserId;
	}

	public User getShippingOutUser() {
		return shippingOutUser;
	}

	public void setShippingOutUser(User shippingOutUser) {
		this.shippingOutUser = shippingOutUser;
	}

	@Override
	public String toString() {
		return "ShippingList [id=" + id + ", title=" + title + ", requirements=" + requirements + ", so=" + so
				+ ", cabinetNo=" + cabinetNo + ", seal=" + seal + ", carLicense=" + carLicense + ", needArrivalTime="
				+ needArrivalTime + ", actualArrivalTime=" + actualArrivalTime + ", leaveTime=" + leaveTime
				+ ", delayReason=" + delayReason + ", status=" + status + ", loadTime=" + loadTime + ", loadUserId="
				+ loadUserId + ", loadUser=" + loadUser + ", shippingOutTime=" + shippingOutTime
				+ ", shippingOutUserId=" + shippingOutUserId + ", shippingOutUser=" + shippingOutUser + "]";
	}
	
}
