package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 *  @author Selien Wicki (314357)
 *  @author Theo Vasarino (313191)
 *
 * class representing the partition of station, final, immutable
 *
 */
public final class StationPartition implements StationConnectivity {

    /**
     * attributs
     */
    private final int[] partition;

    /**
     * private constructor
     * @param partition, tab of int representing the partition
     */
    private StationPartition(int[] partition){
        //copy of the tab by values
        this.partition = partition.clone();
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        int s1Index = s1.id();
        int s2Index = s2.id();

        return (s1Index>= partition.length || s2Index>= partition.length) ?
                s1Index==s2Index : partition[s1Index] == partition[s2Index];
    }

    /**
     * builder of a StationPartition, final, static
     */
    public static final class Builder{

        /**
         * attributs of the builder
         */
        private final int[] builderPartition;

        /**
         * public constructor of the builder
         * @param stationCount the number of station we want to put in the partition
         * @throws IllegalArgumentException if the count is negative
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount>=0);

            builderPartition = new int[stationCount];
            for(int i=0; i<stationCount; ++i){
                builderPartition[i]=i;
            }
        }

        /**
         * connect two station in the partition
         * @param s1 the first station we want to connect
         * @param s2 the second station we want to connect
         * @return the current instance of the builder
         */
        public Builder connect(Station s1, Station s2){
            int s1RepresentativeIndex = representative(s1.id());
            int s2RepresentativeIndex = representative(s2.id());

            builderPartition[s1RepresentativeIndex] = s2RepresentativeIndex;

            return this;
        }

        /**
         * build a StationPartition with a "flat" version of the partition, indeed each station is directly connected to his representative
         * @return the new StationPartition
         */
        public StationPartition build(){
            for(int i=0; i<builderPartition.length; ++i){
                builderPartition[i] = representative(i);
            }
            return new StationPartition(builderPartition);
        }

        /**
         * find and return the index of the representative of a given station id
         * @param id of the station we want to get the representative
         * @return the index of the representative
         */
        private int representative(int id){
            int representativeIndex = id;

            while(builderPartition[representativeIndex] != representativeIndex) {
                representativeIndex = builderPartition[representativeIndex];
            }
            return representativeIndex;
        }
    }
}
