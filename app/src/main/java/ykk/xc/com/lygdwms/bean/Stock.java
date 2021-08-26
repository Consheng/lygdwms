package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

/**
 * 仓库 ( t_Stock )
 */
public class Stock  implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int fstockId;				// 仓库id（金蝶）
    private String fnumber;				// 仓库代码
    private String fname;				// 仓库名称
    private int useStockArea;			// 启用库区
    private int useStorageRack;			// 启用货架
    private int useStockPosition;		// 启用库位
    private String barcode;				// 生成的条码
    private int createUserId;			// 创建用户id
    private String createUserName;		// 创建用户名称
    private String createDate;			// 创建日期

    // 临时字段，不存表
    private String className; // 前端用到的，请勿删除

    public Stock() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFstockId() {
        return fstockId;
    }

    public void setFstockId(int fstockId) {
        this.fstockId = fstockId;
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

    public int getUseStockArea() {
        return useStockArea;
    }

    public void setUseStockArea(int useStockArea) {
        this.useStockArea = useStockArea;
    }

    public int getUseStorageRack() {
        return useStorageRack;
    }

    public void setUseStorageRack(int useStorageRack) {
        this.useStorageRack = useStorageRack;
    }

    public int getUseStockPosition() {
        return useStockPosition;
    }

    public void setUseStockPosition(int useStockPosition) {
        this.useStockPosition = useStockPosition;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }




}
