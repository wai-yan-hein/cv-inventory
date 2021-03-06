/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author Lenovo
 */
@Data
public class FilterObject {

    private String fromDate;
    private String toDate;
    private String cusCode;
    private String vouNo;
    private String userCode;
    private String description;
    private String remark;
    private String vouStatus;
    private String stockCode;
    private String saleManCode;
    private String reference;
    private String locCode;
    @NonNull
    private String compCode;

    public FilterObject(String compCode) {
        this.compCode = compCode;
    }

}
