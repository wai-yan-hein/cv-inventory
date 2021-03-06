/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.user.model.Currency;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author winswe
 */
@Data
public class PurHis implements java.io.Serializable {

    @NonNull
    private String vouNo;
    private Trader trader;
    @NonNull
    private Date vouDate;
    private Date dueDate;
    @NonNull
    private Location location;
    private Boolean deleted;
    @NonNull
    private Float vouTotal;
    private Float paid;
    private Float discount;
    private Float balance;
    @NonNull
    private String createdBy;
    @NonNull
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String remark;
    private Integer session;
    @NonNull
    private Currency currency;
    private Float discP;
    private Float taxP;
    private Float taxAmt;
    private String intgUpdStatus;
    private String reference;
    private Integer macId;
    @NonNull
    private String compCode;
    @NonNull
    private String status = "STATUS";
    private List<PurHisDetail> listPD;
    private List<String> listDel;

    public PurHis() {
    }

}
