/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class ProcessType {

    private ProcessTypeKey key;
    private String userCode;
    private String proName;
    private Integer uniqueId;
    private boolean calculate;
}
