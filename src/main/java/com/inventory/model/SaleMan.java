/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class SaleMan implements Serializable {

    private String saleManCode;
    private String saleManName;
    private Boolean active;
    private String phone;
    private Date updatedDate;
    private Gender genderId;
    private String address;
    private Integer macId;
    private String compCode;
    private String userCode;
    private Date createdDate;
    private String createdBy;
    private String updatedBy;

    public SaleMan() {
    }

    public SaleMan(String saleManCode, String saleManName) {
        this.saleManCode = saleManCode;
        this.saleManName = saleManName;
    }

}
