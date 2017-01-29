package org.musalahuddin.myexpenseorganizer.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    /**
     * <a href="http://www.ibm.com/developerworks/java/library/j-numberformat/">http://www.ibm.com/developerworks/java/library/j-numberformat/</a>
     * @param strFloat parsed as float with the number format defined in the locale
     * @return the float retrieved from the string or null if parse did not succeed
     */
    public static BigDecimal validateNumber(DecimalFormat df, String strFloat) {
        ParsePosition pp;
        pp = new ParsePosition( 0 );
        pp.setIndex( 0 );
        df.setParseBigDecimal(true);
        BigDecimal n = (BigDecimal) df.parse(strFloat,pp);
        if( strFloat.length() != pp.getIndex() ||
                n == null )
        {
            return null;
        } else {
            return n;
        }
    }

    /**
     * Dismiss the keyboard if it is displayed by any of the listed views
     * @param views - a list of views that might potentially be displaying the keyboard
     */
    public static void hideSoftInputForViews(Context context, View...views) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        for (View v : views) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
