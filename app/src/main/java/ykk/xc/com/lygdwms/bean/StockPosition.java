package ykk.xc.com.lygdwms.bean;

import java.io.Serializable;

/**
 * 库位 ( t_StockPosition )
 */
public class StockPosition implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String fnumber;				// 库位编码
    private String fname;				// 库位名称
    private int stockId;				// 仓库id
    private int stockAreaId;			// 库区Id
    private int storageRackId;			// 货架Id
    private String barcode;				// 生成的条码
    private int createUserId;			// 创建用户id
    private String createUserName;		// 创建用户名称
    private String createDate;			// 创建日期

    private Stock stock;
    private StockArea stockArea;
    private StorageRack storageRack;

    // 临时字段，不存表
    private String className; // 前段用到的，请勿删除

    public StockPosition() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(int stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public int getStorageRackId() {
        return storageRackId;
    }

    public void setStorageRackId(int storageRackId) {
        this.storageRackId = storageRackId;
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

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public StockArea getStockArea() {
        return stockArea;
    }

    public void setStockArea(StockArea stockArea) {
        this.stockArea = stockArea;
    }

    public StorageRack getStorageRack() {
        return storageRack;
    }

    public void setStorageRack(StorageRack storageRack) {
        this.storageRack = storageRack;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


}
