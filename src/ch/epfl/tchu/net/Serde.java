package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
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

    // ! Je suis pas sur d'avoir bien compris la donnee.. !
    static <T> Serde<T> oneOf(List<T> enumList){
        return new Serde<T>(){
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
