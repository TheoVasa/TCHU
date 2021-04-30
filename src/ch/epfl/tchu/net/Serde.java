package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * This interface represent a serde, used to serialize and deserialize some information.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public interface Serde<E> {

    /**
     * This method serialize the data information of the game.
     *
     * @param e the data element we want to serialize
     * @return The serialized data element (String)
     */
    String serialize(E e);

    /**
     * This method takes a given data of information that was serialized and deserialized it
     *
     * @param data the data information we want to deserialize
     * @return the deserialized <code>data</code> (E)
     */
    E deserialize(String data);

    /**
     * Create a new Serde from given serialization and deserialization functions
     *
     * @param serialization the function that the future serde will use to serialize the data
     * @param deserialization the function that the future serde will use to serialize the data
     * @param <T> the parameters associate to the future serde
     * @return a new Serde with the given serialization and deserialization functions (Serde)
     */
    static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization){
        return new Serde<T>() {
            @Override
            public String serialize(T t) {
                return serialization.apply(t);
            }

            @Override
            public T deserialize(String data) {
                return deserialization.apply(data);
            }
        };
    }

    /**
     * Create a new Serde from a given enumeration of objects (Enum types, all tickets in the game, all the routes, etc..).
     *
     * @param enumList the list of all objects.
     * @param <T> the type of the object.
     * @return the new Serde for the given enum (Serde).
     * //TODO --> throw illegal si la liste est vie
     */
    static <T> Serde<T> oneOf(List<T> enumList) {
        return Serde.of(
                //serialization function
                (T t) -> (t != null && enumList.contains(t))
                        ? String.valueOf(enumList.indexOf(t))
                        : "",
                //deserialization function
                (String data) ->  (!data.isEmpty())
                                    ? enumList.get(Integer.parseInt(data))
                                    : null
        );
    }

    /**
     * Create a new Serde who's able to (de)serialize with the given separator a list of objects (de)serialized with the given serde.
     *
     * @param serde used to (de)serialize the objects in the future List.
     * @param separator used to separate all the different objects in the list in the serialization and deserialization.
     * @param <T> type of the objects.
     * @return the new Serde (Serde).
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator){
        return Serde.of(
                //serialization function
                (List<T> l) -> {
                    List<String> dataList = new ArrayList<>();
                    for(T t : l)
                        dataList.add(serde.serialize(t));
                    return String.join(separator, dataList);
                },
                //deserialization function
                (String data) -> {
                    //If data is empty then return an empty list
                    if (data.isEmpty())
                        return new ArrayList<T>();
                    //If data is not empty then deserialize element by element
                    List<T> deserializedList = new ArrayList<>();
                    String[] splitData = data.split(Pattern.quote(separator), -1);
                    for(String str : splitData)
                        deserializedList.add(serde.deserialize(str));
                    return deserializedList;
                }
        );
    }

    /**
     * Create a new Serde who's able to (de)serialize with the given separator a bag of objects (de)serialized with the given serde.
     *
     * @param serde used to (de)serialize the objects in the future bag.
     * @param separator used to separate all the different objects in the bag in the serialization and deserialization.
     * @param <T> type of the objects.
     * @return the new Serde (Serde).
     */
    //peut etre opti avec une methode privée qui transform un serde de list en serde de SortedBag, mais bon.. a faire
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator){
        return Serde.of(
                //serialization function
                (SortedBag<T> bag)-> Serde.listOf(serde, separator).serialize(bag.toList()),
                //deserialization function
                (String data) -> SortedBag.of(Serde.listOf(serde, separator).deserialize(data))
        );
    }
}
