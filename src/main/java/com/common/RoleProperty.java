/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class RoleProperty implements Serializable {

    private RolePropertyKey key;
    private String propValue;
    private String remark;
    private String compCode;

    public RoleProperty() {
    }

    public RoleProperty(RolePropertyKey key, String propValue) {
        this.key = key;
        this.propValue = propValue;
    }

}
