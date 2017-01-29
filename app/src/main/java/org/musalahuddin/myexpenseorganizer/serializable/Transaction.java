package org.musalahuddin.myexpenseorganizer.serializable;

import java.io.Serializable;

public class Transaction implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long id;
    public long primaryAccountId;
    public String primaryAccountName;
    public String primaryAccountDescription;
    public long secondaryAccountId;
    public String secondaryAccountName;
    public String secondaryAccountDescription;
    public double amount;
    public int isDeposit;
    public String notes;
    public String imagePath;
    public long date;
    public long transactionCategoryId;
    public long expenseCategoryId;
    public String transactionCategoryName;
    public String expenseCategoryParentName;
    public String expenseCategoryChildName;
}
