package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.function.Function;

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

    // ! Pour l'instant j'ai mis  E mais je suis pas sur d'avoir bien compris la donnee.. !
    static <E> Serde<E> of(Function<E, String> serialization, Function<String, E> deserialization){
        //TODO
    }

    // ! Je suis pas sur d'avoir bien compris la donnee.. !
    static Serde<E> oneOf(List<E> enumList){
        //TODO
    }

    // ! Je suis pas sur d'avoir bien compris la donnee.. !
    static <T> Serde<T> listOf(Serde<List<T>> serde, String separator){
        //TODO
    }

    // ! Je suis pas sur d'avoir bien compris la donnee.. !
    static <T extends Comparable<T>> Serde<T> bagOf(Serde<SortedBag<T>> serde, String separator){
        //TODO
    }
}
