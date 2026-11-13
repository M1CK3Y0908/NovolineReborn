package wtf.novoline.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoGroup {
   NoGroup.BeforeKeyType beforeKeyType() default NoGroup.BeforeKeyType.AFTER_KEY;

   String value() default "";

   enum BeforeKeyType {
      BEFORE_KEY,
      AFTER_KEY,
      NO_GROUP;
   }
}
