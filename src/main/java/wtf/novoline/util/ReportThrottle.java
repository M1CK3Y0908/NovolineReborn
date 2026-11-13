package wtf.novoline.util;

import java.util.HashMap;
import java.util.Map;

public class ReportThrottle {
   private static Map<String, Long> lastReportTimes = new HashMap<>();

   public static boolean shouldReport(String text) {
      synchronized (lastReportTimes) {
         long l = System.currentTimeMillis();
         Long lValue = lastReportTimes.get(text);
         if (lValue == null) {
            lValue = new Long(l);
            lastReportTimes.put(text, lValue);
         }

         long l2 = lValue;
         if (l < l2 + 10000L) {
            return false;
         }

         lastReportTimes.put(text, new Long(l));
         return true;
      }
   }
}
