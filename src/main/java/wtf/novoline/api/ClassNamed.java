package wtf.novoline.api;

public interface ClassNamed<V> {
   String getName();

   boolean accepts(V value);

   String getValueName(V value);

   Class<V> getType();
}
