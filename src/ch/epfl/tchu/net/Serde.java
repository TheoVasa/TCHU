package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

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
    // ! Pour l'instant j'ai mis  E mais je suis pas sur d'avoir bien compris la donnee.. !
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
     */
    //ici j'ai refait la methode à ma manière, jai enlevé le throw parce que je comprenais pas exactement ce quil faisait, donc a toi de le rajouter dans la lambda si il est important!
    static <T> Serde<T> oneOf(List<T> enumList) {
        return Serde.of(
                //serialization function
                (T t) -> (enumList.contains(t))
                        ? String.valueOf(enumList.indexOf(t))
                        : "",
                //deserialization function
                (String data) -> enumList.get(Integer.parseInt(data))
        );
    }
/*
        );{
            @Override
            public String serialize(T t) {
                String data = (enumList.contains(t))
                        ? new StringBuilder()
                            .append(enumList.indexOf(t))
                            .toString()
                        : "";
                return data;
            }

            @Override
            public T deserialize(String data) {
                //Take the index of the single data
                int index = 0;
                try {
                    index = Integer.parseInt(data);
                } catch (NumberFormatException e) {
                    Preconditions.checkArgument(false);
                } //DO NOTHING

                T element = enumList.get(index);
                return element;
            }
        };
 */

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
                //peut etre amelioré ici (jai pensé a transformer tout les elements de la liste en String avec lambda forEach et apres return le join de tout ca)
                (List<T> l) -> {
                    /*
                    l.forEach(serde::serialize);
                    return String.join(separator, (CharSequence) l);
                    */
                    String data = "";
                    for(T t : l)
                        data = String.join(separator, serde.serialize(t));
                    return data;
                },
                //deserialization function
                //peu etre ameliore aussi (surtout noms de variables etc..)
                (String data) -> {
                    List<T> deserList = new ArrayList<>();
                    String[] splitData = data.split(Pattern.quote(separator), -1);
                    for(String str : splitData)
                        deserList.add(serde.deserialize(str));
                    return deserList;
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
                (SortedBag<T> bag)-> {
                    List<T> listFromBag = bag.toList();
                    String data = "";
                    for(T t : listFromBag)
                        data = String.join(separator, serde.serialize(t));
                    return data;
                },
                //deserialization function
                (String data) -> {
                    List<T> listOfObj = new ArrayList<>();
                    String[] allString = data.split(Pattern.quote(separator), -1);
                    for(String str : allString)
                        listOfObj.add(serde.deserialize(str));
                    return SortedBag.of(listOfObj);
                }
        );
    }


}
