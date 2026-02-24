package lk.icbt.findit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static Date getCurrentDateByTimeZone(String timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        SimpleDateFormat dateFormatConf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatConf.setTimeZone(TimeZone.getTimeZone(timeZone));


        Date date = new Date();
        Date confTime = null;
        try {
            confTime = dateFormat.parse(dateFormatConf.format(date));
        } catch (ParseException exception) {
            log.warn("Unable to obtain TimeZone date using local date, defaulting to local date", exception);
            confTime = date;
        }
        return confTime;
    }
}
