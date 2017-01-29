package org.musalahuddin.myexpenseorganizer.serializable;

import java.io.Serializable;

public class Budget implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long id;
    public long accountId;
    public String accountName;
    public double amount;
    public String notes;
    public long expenseCategoryId;
    public String expenseCategoryParentName;
    public String expenseCategoryChildName;
}
