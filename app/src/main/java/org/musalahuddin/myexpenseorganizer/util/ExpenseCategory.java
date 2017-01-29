package org.musalahuddin.myexpenseorganizer.util;

import java.util.ArrayList;

public class ExpenseCategory extends Category{

    private ArrayList<ExpenseSubCategory> subCategories;

    public ExpenseCategory(){
        super();
        subCategories = new ArrayList<ExpenseSubCategory>();
    }


    public ArrayList<ExpenseSubCategory> getSubCategories(){
        return subCategories;
    }

    public void addSubCategory(ExpenseSubCategory subCategory){
        subCategories.add(subCategory);
    }

}
