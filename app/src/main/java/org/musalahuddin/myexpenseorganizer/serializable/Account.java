package org.musalahuddin.myexpenseorganizer.serializable;

import java.io.Serializable;

/**
 * Created by Muhammad on 12/23/2016.
 */

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    public long id;
    public String name;
    public int number;
    public double balance;
    public double limit;
    public double payment;
    public String description;
    public long due;
    public long budget_start_day;
    public long accoutCategoryId;
    public String accountCategoryName;

}
