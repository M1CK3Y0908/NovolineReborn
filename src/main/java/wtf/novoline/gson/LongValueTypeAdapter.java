package wtf.novoline.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import wtf.novoline.client.discord.Snowflake;

public class LongValueTypeAdapter extends TypeAdapter<Snowflake> {
   public void write(JsonWriter jsonWriter, Snowflake snowflake) throws IOException {
      jsonWriter.value(snowflake.getLongValue());
   }

   public Snowflake read(JsonReader jsonReader) throws IOException {
      return new Snowflake(jsonReader.nextLong());
   }
}
