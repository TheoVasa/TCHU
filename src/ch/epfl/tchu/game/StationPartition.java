package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity {

    private final int[] partition;



    private StationPartition(int[] partition){
        this.partition = partition;
}

    @Override
    public boolean connected(Station s1, Station s2) {

        int s1Index = ChMap.stations().indexOf(s1);
        int s2Index = ChMap.stations().indexOf(s2);

        if(s1Index> partition.length || s2Index> partition.length)
            return s1Index==s2Index;
        else
        return partition[s1Index] == partition[s2Index];
    }

    public static final class Builder{

        private final int[] BuilderPartition;

        public Builder(int count) {
            Preconditions.checkArgument(count<0);




        }
    }



}
