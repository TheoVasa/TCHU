package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * This class represent the connectivity network of the player, in the form of a partition of stations, indeed she implements the interface StationConnectivity, this class is final, public and immutable.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class StationPartition implements StationConnectivity {

    /**
     * Attributes
     */
    //the partition of the network.
    private final int[] partition;

    /**
     * private constructor
     *
     * @param partition, tab of int representing the partition
     */
    private StationPartition(int[] partition) {
        //copy of the tab by values
        this.partition = partition.clone();
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        int s1Index = s1.id();
        int s2Index = s2.id();

        //return of the two station have the same "representative" or if the station aren't in the current partition, return true if and only if the station are the same.
        return (s1Index >= partition.length || s2Index >= partition.length)
                ? s1Index == s2Index
                : partition[s1Index] == partition[s2Index];
    }

    /**
     * Builder of a StationPartition, final, static and nested in the class StationPartition
     *
     * @author Selien Wicki (314357)
     * @author Theo Vasarino (313191)
     */
    public static final class Builder {

        /**
         * Attributes
         */

        //the first version of the partition
        private final int[] builderPartition;

        /**
         * Construct a Builder with a certain number of Stations.
         * @param stationCount the number of station we want to put in the partition.
         * @throws IllegalArgumentException if the count is strictly negative.
         */
        public Builder(int stationCount) {
            //Check the correctness of the argument.
            Preconditions.checkArgument(stationCount >= 0);

            //at the beginning, all stations have itself for representative.
            builderPartition = new int[stationCount];
            for (int i = 0; i < stationCount; ++i) {
                builderPartition[i] = i;
            }
        }

        /**
         * Use to connect two stations in the partition.
         * @param s1 the first station we want to connect.
         * @param s2 the second station we want to connect.
         * @return the current instance of the builder, with the two station connected (indeed the representative of one is setup like the representative of the other). (Builder)
         */
        public Builder connect(Station s1, Station s2) {
            int s1RepresentativeIndex = representative(s1.id());
            int s2RepresentativeIndex = representative(s2.id());
            builderPartition[s1RepresentativeIndex] = s2RepresentativeIndex;

            return this;
        }

        /**
         * Build a StationPartition with a "flat" version of the partition, indeed each station is directly connected to his representative.
         *
         * @return the new StationPartition. (StationPartition)
         */
        public StationPartition build() {
            for (int i = 0; i < builderPartition.length; ++i) {
                builderPartition[i] = representative(i);
            }
            return new StationPartition(builderPartition);
        }


        //Find and return the index of the representative of a given station id.
        private int representative(int id) {
            int representativeIndex = id;

            while (builderPartition[representativeIndex]
                    != representativeIndex) {
                representativeIndex = builderPartition[representativeIndex];
            }
            return representativeIndex;
        }
    }
}
