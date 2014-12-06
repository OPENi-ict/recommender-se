package control.places.checkins;

import java.util.Calendar;

public class CheckinUtilities {
	
    public static boolean isWeekday(long timestamp){
    	
    	Calendar cal = Calendar.getInstance();
    	//java expects milliseconds
    	cal.setTimeInMillis(timestamp*1000);
    	int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    	if ((dayOfWeek==1)||(dayOfWeek==7))
    		return false;
    	return true;
    }
    
    //Sunday code is 1
    public static int getDayCode(long timestamp){
    	Calendar cal = Calendar.getInstance();
    	//java expects milliseconds
    	cal.setTimeInMillis(timestamp*1000);
    	return Calendar.DAY_OF_WEEK;
    }

}
