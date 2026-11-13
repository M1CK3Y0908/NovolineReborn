package net.minecraft.util;

import net.minecraft.crash.CrashReport;

public class ReportedException extends RuntimeException {
   private final CrashReport theReportedExceptionCrashReport;

   @Override
   public Throwable getCause() {
      return this.theReportedExceptionCrashReport.getCrashCause();
   }

   public CrashReport getCrashReport() {
      return this.theReportedExceptionCrashReport;
   }

   public ReportedException(CrashReport report) {
      this.theReportedExceptionCrashReport = report;
   }

   @Override
   public String getMessage() {
      return this.theReportedExceptionCrashReport.getDescription();
   }
}
