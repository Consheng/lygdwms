package ykk.xc.com.lygdwms.bean.k3Bean;

import java.io.Serializable;
/**
 * 发货通知单
 * @author Administrator
 *
 */
public class DeliveryNotice implements Serializable {
	private static final long serialVersionUID = 1L;

	private int fid; 						// 单据id
	private String fbillNo; 				// 单据编号
	private String fbillTypeId; 			// 单据类型
	private String billType_fnumber;		// 单据类型代码
	private String billType_fname;			// 单据类型名称
	private String fdate; 					// 日期
	private int fcustomerId; 				// 客户Id
	private int fdeliveryOrgId;				// 发货组织id
	private int fdeliveryDeptId;			// 发货部门id
	private int fstockerId;					// 发货员id
	private int fsaleOrgId; 				// 销售组织id
	private int fsaleDeptId;				// 销售部门id
	private int fsalesManId;				// 销售员id
	private String fdocumentStatus;			// 单据状态（A:新建， Z:暂存， B:审核中 ，C:已审核 ，D:重新审核 ）
	private String fcloseStatus;			// 关闭状态--A、正常；B、已关闭
	private String fcancelStatus;			// 作废标志：A：未作废 B：已作废
	private String fbusinessType;			// 业务类型（NORMAL）
	private String fobjectTypeId;			// 业务对象类型：SAL_SaleOrder
	private String deliveryTypeId;			// 交货类别id
	private String deliveryTypeNumber;		// 交货类别代码
	private String deliveryTypeName;		// 交货类别代码
	private String deliveryWayId;			// 发货方式id
	private String deliveryWayNumber;		// 发货方式代码
	private String deliveryWayName;			// 发货方式名称
	private String receiveAddress;			// 交货地点
	private String receiveContact;			// 收方联系人
	private String expressNo;				// 运输单号
	private String receiveTel;				// 收方电话
	
	private Organization_K3 deliOrg;
	private Customer_K3 deliCust;
	private Department_K3 deliDept;
	private Operator_K3 deliMan;
	private Organization_K3 saleOrg;
	private Department_K3 saleDept;
	private Operator_K3 saleMan;

	// 临时字段，不存表
	private boolean check;	// 是否选中
	private double sumQty;	//采购入库计算的总数
	private boolean wmsUploadStatus; // WMS上传状态
	
	public DeliveryNotice() {
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

	public String getFbillTypeId() {
		return fbillTypeId;
	}

	public void setFbillTypeId(String fbillTypeId) {
		this.fbillTypeId = fbillTypeId;
	}

	public String getBillType_fnumber() {
		return billType_fnumber;
	}

	public void setBillType_fnumber(String billType_fnumber) {
		this.billType_fnumber = billType_fnumber;
	}

	public String getBillType_fname() {
		return billType_fname;
	}

	public void setBillType_fname(String billType_fname) {
		this.billType_fname = billType_fname;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public int getFcustomerId() {
		return fcustomerId;
	}

	public void setFcustomerId(int fcustomerId) {
		this.fcustomerId = fcustomerId;
	}

	public int getFdeliveryOrgId() {
		return fdeliveryOrgId;
	}

	public void setFdeliveryOrgId(int fdeliveryOrgId) {
		this.fdeliveryOrgId = fdeliveryOrgId;
	}

	public int getFdeliveryDeptId() {
		return fdeliveryDeptId;
	}

	public void setFdeliveryDeptId(int fdeliveryDeptId) {
		this.fdeliveryDeptId = fdeliveryDeptId;
	}

	public int getFstockerId() {
		return fstockerId;
	}

	public void setFstockerId(int fstockerId) {
		this.fstockerId = fstockerId;
	}

	public int getFsaleOrgId() {
		return fsaleOrgId;
	}

	public void setFsaleOrgId(int fsaleOrgId) {
		this.fsaleOrgId = fsaleOrgId;
	}

	public int getFsaleDeptId() {
		return fsaleDeptId;
	}

	public void setFsaleDeptId(int fsaleDeptId) {
		this.fsaleDeptId = fsaleDeptId;
	}

	public int getFsalesManId() {
		return fsalesManId;
	}

	public void setFsalesManId(int fsalesManId) {
		this.fsalesManId = fsalesManId;
	}

	public String getFdocumentStatus() {
		return fdocumentStatus;
	}

	public void setFdocumentStatus(String fdocumentStatus) {
		this.fdocumentStatus = fdocumentStatus;
	}

	public String getFcloseStatus() {
		return fcloseStatus;
	}

	public void setFcloseStatus(String fcloseStatus) {
		this.fcloseStatus = fcloseStatus;
	}

	public String getFcancelStatus() {
		return fcancelStatus;
	}

	public void setFcancelStatus(String fcancelStatus) {
		this.fcancelStatus = fcancelStatus;
	}

	public String getFbusinessType() {
		return fbusinessType;
	}

	public void setFbusinessType(String fbusinessType) {
		this.fbusinessType = fbusinessType;
	}

	public String getFobjectTypeId() {
		return fobjectTypeId;
	}

	public void setFobjectTypeId(String fobjectTypeId) {
		this.fobjectTypeId = fobjectTypeId;
	}

	public String getDeliveryTypeId() {
		return deliveryTypeId;
	}

	public void setDeliveryTypeId(String deliveryTypeId) {
		this.deliveryTypeId = deliveryTypeId;
	}

	public String getDeliveryTypeNumber() {
		return deliveryTypeNumber;
	}

	public void setDeliveryTypeNumber(String deliveryTypeNumber) {
		this.deliveryTypeNumber = deliveryTypeNumber;
	}

	public String getDeliveryTypeName() {
		return deliveryTypeName;
	}

	public void setDeliveryTypeName(String deliveryTypeName) {
		this.deliveryTypeName = deliveryTypeName;
	}

	public String getDeliveryWayId() {
		return deliveryWayId;
	}

	public void setDeliveryWayId(String deliveryWayId) {
		this.deliveryWayId = deliveryWayId;
	}

	public String getDeliveryWayNumber() {
		return deliveryWayNumber;
	}

	public void setDeliveryWayNumber(String deliveryWayNumber) {
		this.deliveryWayNumber = deliveryWayNumber;
	}

	public String getDeliveryWayName() {
		return deliveryWayName;
	}

	public void setDeliveryWayName(String deliveryWayName) {
		this.deliveryWayName = deliveryWayName;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getReceiveContact() {
		return receiveContact;
	}

	public void setReceiveContact(String receiveContact) {
		this.receiveContact = receiveContact;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public String getReceiveTel() {
		return receiveTel;
	}

	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}

	public Organization_K3 getDeliOrg() {
		return deliOrg;
	}

	public void setDeliOrg(Organization_K3 deliOrg) {
		this.deliOrg = deliOrg;
	}

	public Customer_K3 getDeliCust() {
		return deliCust;
	}

	public void setDeliCust(Customer_K3 deliCust) {
		this.deliCust = deliCust;
	}

	public Department_K3 getDeliDept() {
		return deliDept;
	}

	public void setDeliDept(Department_K3 deliDept) {
		this.deliDept = deliDept;
	}

	public Operator_K3 getDeliMan() {
		return deliMan;
	}

	public void setDeliMan(Operator_K3 deliMan) {
		this.deliMan = deliMan;
	}

	public Organization_K3 getSaleOrg() {
		return saleOrg;
	}

	public void setSaleOrg(Organization_K3 saleOrg) {
		this.saleOrg = saleOrg;
	}

	public Department_K3 getSaleDept() {
		return saleDept;
	}

	public void setSaleDept(Department_K3 saleDept) {
		this.saleDept = saleDept;
	}

	public Operator_K3 getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(Operator_K3 saleMan) {
		this.saleMan = saleMan;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public double getSumQty() {
		return sumQty;
	}

	public void setSumQty(double sumQty) {
		this.sumQty = sumQty;
	}

	public boolean isWmsUploadStatus() {
		return wmsUploadStatus;
	}

	public void setWmsUploadStatus(boolean wmsUploadStatus) {
		this.wmsUploadStatus = wmsUploadStatus;
	}

	
}
