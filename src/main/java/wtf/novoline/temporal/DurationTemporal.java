package wtf.novoline.temporal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class DurationTemporal implements TemporalAccessor {
   private static final Temporal temporal = LocalDateTime.of(0, 1, 1, 0, 0);
   private final Duration duration;
   private final Temporal temporal2;
   private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
      .optionalStart()
      .optionalStart()
      .optionalStart()
      .optionalStart()
      .optionalStart()
      .optionalStart()
      .appendValue(ChronoField.YEAR)
      .appendLiteral(" Years ")
      .optionalEnd()
      .appendValue(ChronoField.MONTH_OF_YEAR)
      .appendLiteral(" Months ")
      .optionalEnd()
      .appendValue(ChronoField.DAY_OF_MONTH)
      .appendLiteral(" Days ")
      .optionalEnd()
      .appendValue(ChronoField.HOUR_OF_DAY)
      .appendLiteral(" Hours ")
      .optionalEnd()
      .appendValue(ChronoField.MINUTE_OF_HOUR)
      .appendLiteral(" Minutes ")
      .optionalEnd()
      .appendValue(ChronoField.SECOND_OF_MINUTE)
      .appendLiteral(" Seconds")
      .optionalEnd()
      .toFormatter();

   @Override
   public long getLong(TemporalField temporalField) {
      if (!this.isSupported(temporalField)) {
         throw new UnsupportedTemporalTypeException(temporalField.toString());
      } else {
         return this.temporal2.getLong(temporalField) - temporal.getLong(temporalField);
      }
   }

   public DurationTemporal(Duration duration2) {
      this.duration = duration2;
      this.temporal2 = duration2.addTo(temporal);
   }

   @Override
   public String toString() {
      return dateTimeFormatter.format(this);
   }

   public Duration getDuration() {
      return this.duration;
   }

   @Override
   public boolean isSupported(TemporalField temporalField) {
      return this.temporal2.isSupported(temporalField) && this.temporal2.getLong(temporalField) - temporal.getLong(temporalField) != 0L;
   }
}
