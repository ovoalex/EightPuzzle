import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStar {
    private PriorityQueue<StateNode> fringe;
    private HashSet<StateNode> exploredSet;
    private final Integer[] goal = {0,1,2,3,4,5,6,7,8};
    /**
     * The A* algo on a specific board state using either the misplaced tile or sum of distance heuristic.
     */
    public StateNode runAStar(StateNode initialState, boolean isH1, boolean print){
        int searchCost = 0;
        exploredSet = new HashSet<>();
        if(isH1){
            fringe = new PriorityQueue<>((final StateNode o1, final StateNode o2) -> 
            	(o1.getCost()+misplacedTiles(o1)) - (o2.getCost()+misplacedTiles(o2)));
        }else{
            fringe = new PriorityQueue<>((final StateNode o1, final StateNode o2) 
            		-> (o1.getCost()+sumOfDistance(o1)) - (o2.getCost()+sumOfDistance(o2)));            
        }
        fringe.add(initialState);
        while(!fringe.isEmpty()){
            StateNode current = fringe.poll();
            exploredSet.add(current);    
            if(print == true){
                System.out.println(current);
                System.out.println("Step: " + current.getCost());              
            }
            if(Arrays.equals(current.getCurrentState(), goal)){
                System.out.println("----------- GOAL FOUND AT SEARCH COST OF " + searchCost + "------------");
                 return current;
            }     
            ArrayList<StateNode> children = current.expandCurrentNode();
            for(int i = 0; i < children.size(); ++i){
                if(!exploredSet.contains(children.get(i))){
                    searchCost++;
                    children.get(i).setSearchCost(searchCost);
                    fringe.add(children.get(i));
                }
            }
        }
        return null;
    }  
    /**
     * H1 Algorithm
     * Heuristic function that simply counts the number of misplaced tiles and returns the total amount of misplaced tiles
     */
    public int misplacedTiles(StateNode node){
        int misplaced = 0;
        for(int i = 0; i < node.getCurrentState().length; ++ i){
            if(node.getCurrentState()[i] != i) misplaced++;
        }
        return misplaced;
    }
    
    /**
     * H2 Algorithm
     * Heuristic function for the Manhattan distance. The sum of the distance from the tile to the goal and returns the sum of the distances
     */
    public int sumOfDistance(StateNode node){
        int sum = 0;
        for(int i = 0; i < node.getCurrentState().length; ++i){
            if(node.getCurrentState()[i] == i) continue;
            if(node.getCurrentState()[i] == 0) continue;
            int row = node.getCurrentState()[i]/3;
            int col = node.getCurrentState()[i]%3;
            int goalRow = i/3;
            int goalCol = i%3;
            sum += Math.abs(col - goalCol) +  Math.abs(row - goalRow);
        }
        return sum;
    }    
}