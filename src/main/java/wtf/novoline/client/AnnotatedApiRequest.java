package wtf.novoline.client;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.QueryType;
import wtf.novoline.utils.http.FormRequestBody;
import wtf.novoline.utils.http.JsonRequestBody;
import wtf.novoline.utils.http.RequestBody;

public abstract class AnnotatedApiRequest extends ApiRequest {
   @Override
   public String getPath() {
      String path2 = super.getPath();

      for (Field field2 : this.getClass().getDeclaredFields()) {
         if (field2.isAnnotationPresent(PathParam.class)) {
            PathParam annotation2 = field2.getAnnotation(PathParam.class);
            if (annotation2.enabled()) {
               try {
                  field2.setAccessible(true);
                  Object object = field2.get(this);
                  if (object == null) {
                     throw new IllegalArgumentException("Value of required path variable " + annotation2.value() + " is null");
                  }

                  path2 = path2.replace("{" + annotation2.value() + "}", String.valueOf(object));
               } catch (Exception exception) {
                  throw new RuntimeException("Unable to map field " + field2.getName(), exception);
               }
            }
         }
      }

      return path2;
   }

   public AnnotatedApiRequest() {
      Class classValue = this.getClass();
      if (classValue.isAnnotationPresent(Route.class)) {
         Route route = (Route) classValue.getAnnotation(Route.class);
         this.requestMethod(route.httpMethod());
         this.withPath(route.value());
      } else {
         throw new IllegalArgumentException("@Details annotation is not present");
      }
   }

   @Override
   public RequestBody getRequestBody() {
      Class classValue = this.getClass();
      if (classValue.isAnnotationPresent(Query.class)) {
         QueryType value2 = ((Query)classValue.getAnnotation(Query.class)).value();
         if (value2 == QueryType.QUERY) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();

            for (Field field2 : classValue.getDeclaredFields()) {
               try {
                  field2.setAccessible(true);
                  if (field2.isAnnotationPresent(PathParam.class)) {
                     PathParam annotation2 = field2.getAnnotation(PathParam.class);
                     if (!annotation2.enabled()) {
                        Object object = field2.get(this);
                        if (object != null) {
                           linkedHashMap.put(annotation2.value(), String.valueOf(object));
                        }
                     }
                  }
               } catch (Exception exception) {
                  throw new RuntimeException("Unable to map field " + field2.getName(), exception);
               }
            }

            return new FormRequestBody(linkedHashMap);
         }

         if (value2 == QueryType.JSON) {
            return JsonRequestBody.createJsonRequestBody(this);
         }
      }

      return super.getRequestBody();
   }
}
