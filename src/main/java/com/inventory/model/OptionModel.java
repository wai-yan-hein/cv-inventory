/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class OptionModel {

    private String code;
    private String name;
    private boolean selected;

    public OptionModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
