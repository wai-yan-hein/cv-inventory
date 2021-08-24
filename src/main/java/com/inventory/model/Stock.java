/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Stock implements java.io.Serializable {

    private String stockCode;
    private Boolean isActive;
    private StockBrand brand;
    private String stockName;
    private Category category;
    private StockType stockType;
    private AppUser createdBy;
    private AppUser updatedBy;
    private String barcode;
    private String shortName;
    private Float purWeight;
    private Float purPrice;
    private StockUnit purUnit;
    private Float saleWeight;
    private StockUnit saleUnit;
    private Date expireDate;
    private String remark;
    private Float salePriceN;
    private Float salePriceA;
    private Float salePriceB;
    private Float salePriceC;
    private Float salePriceD;
    private Float sttCostPrice;
    private Date updatedDate;
    private Date createdDate;
    private String migCode;
    private String compCode;
    private String userCode;
    private Integer macId;

}
