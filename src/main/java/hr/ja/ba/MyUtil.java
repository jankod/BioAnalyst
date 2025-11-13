package hr.ja.ba;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MyUtil {
    private static final DateTimeFormatter formatter =
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")
                .withZone(ZoneId.systemDefault());

    public static String format(Instant dateTime) {
        return formatter.format(dateTime);
    }
}
