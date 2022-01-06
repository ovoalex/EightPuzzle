import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EightPuzzle {

    public static void main(String[] args) {
    	boolean loop = false;
        Integer userInput = 1;
        System.out.println("------- Single Test Puzzle -------\n");
        System.out.println("Select Input Method:\n");
        String start = "1) Random Eight-Puzzle\n2) User Input Eight-Puzzle\n3) Import File Puzzle\n4) Exit";
        while(!loop){
            System.out.println(start);
            Scanner kb = new Scanner(System.in);
            String userChoice = kb.nextLine();
            try{
                userInput = Integer.parseInt(userChoice);
                if(userInput > -1 && userInput <=4) break;
                else
                    System.out.println("Input is not a valid option");
            }catch(NumberFormatException e) {
                System.out.println("Input is not a number. Please try again.");
            }
        }
        EightPuzzle UserCases = new EightPuzzle();
        switch (userInput) {
            case 1:
                UserCases.randomPuzzle();
                break;
            case 2:
                UserCases.userInputPuzzle();
                break;
            case 3:
                UserCases.importPuzzle();
                break;
            case 4:
                System.exit(0);
            default:
                break;
        }
    }
    private AStar aStar = new AStar();
    
    public void randomPuzzle(){
        Map<Integer,ArrayList<SearchData>> runtimeData = new TreeMap<>();
        int timesToRun = -1;
        System.out.println("Enter the amount of time to run. ");
        Scanner kb = new Scanner(System.in);
        while(timesToRun < 1){
            String num = kb.nextLine();
            try{
                timesToRun = Integer.parseInt(num);
            }catch(NumberFormatException e){
                System.out.println("Not a number.");
                timesToRun = -1;
            }
            if(timesToRun < 1) System.out.println("How many times do you want to run this? (>0)");
        }
        File random = new File(timesToRun + "_TestCases.txt");
        BufferedWriter bw = null;
        try {
            random.createNewFile();
            bw = new BufferedWriter(new FileWriter(random));
        } catch (IOException ex) {
            
        }
        for(int i = 0; i < timesToRun; ++i){
            Puzzle puzzle = new RandomPuzzle();
            try {
                bw.write(puzzle.getInitialStateNode().toString().replace(" ","").replace("\n",""));
                bw.newLine();
            } catch (IOException ex) {
            }
            SearchData compute = solve(puzzle.getInitialStateNode());
            if(!runtimeData.containsKey(compute.depth)){
                runtimeData.put(compute.depth, new ArrayList<>());
            }
            runtimeData.get(compute.depth).add(compute);
        }
        try {
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(EightPuzzle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void userInputPuzzle(){
        Puzzle puzzle = new InputPuzzle();
        boolean madePuzzle = false;
        while(!madePuzzle){
            System.out.println("Enter a new puzzle:");
            Scanner kb = new Scanner(System.in);
            String sPuzzle = "";
            String r1 = kb.nextLine().replace(" ", "");
            if(r1.length() == 9){
                sPuzzle = r1;
            }else{
                String r2 = kb.nextLine().replace(" ", "");
                String r3 = kb.nextLine().replace(" ", "");    
                sPuzzle = r1.replace("\n", "") + r2.replace("\n", "") + r3.replace("\n", ""); 
            }
            madePuzzle = puzzle.createPuzzle(sPuzzle);
        }
        StateNode init = puzzle.getInitialStateNode();
        SearchData compute = solve(init);
    }
    
    public void importPuzzle(){
        Map<Integer,ArrayList<SearchData>> runtimeData = new TreeMap<>();
        ArrayList<String> puzzleList = new ArrayList<>();
        String fileStr = "";
        Puzzle puzzle = new InputPuzzle();
        System.out.println("Enter the name of the text file.");
        final String dir = System.getProperty("user.dir");
        System.out.println("The file path is " + dir);
        Scanner kb = new Scanner(System.in);
        fileStr = kb.nextLine();
        try{
            File file = new File(fileStr);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            try {
                while((line = br.readLine()) != null){
                    if(Character.isDigit(line.charAt(0))){
                        puzzleList.add(line);
                    }
                }
                br.close();
                fr.close();
            } catch (IOException ex) {
                System.out.println("IO Exception Occured!");
                System.exit(0);
            }
        }catch(FileNotFoundException e){
            System.out.println("File Not Found!");
            System.exit(0);
        }
        if(puzzleList.size() > 0){
            for(String line : puzzleList){
                boolean madePuzzle = puzzle.createPuzzle(line);
                if(madePuzzle){
                    SearchData compute = solve(puzzle.getInitialStateNode());
                    if(!runtimeData.containsKey(compute.depth)){
                        runtimeData.put(compute.depth, new ArrayList<>());
                    }
                    runtimeData.get(compute.depth).add(compute);
                }
            }
        }
    }
    
    private SearchData solve(StateNode init){     
        System.out.println(init);
        long start1 = System.currentTimeMillis();
        StateNode goalNode1 = aStar.runAStar(init, true, true);
        long end1 = System.currentTimeMillis();
        long total1 = end1 - start1;
        System.out.println("-------------------- FINISHED H1 ------------------------");
        long start2 = System.currentTimeMillis();
        StateNode goalNode2 = aStar.runAStar(init, false, true);
        long end2 = System.currentTimeMillis();
        long total2 = end2 - start2;
        System.out.println("-------------------- FINISHED H2 ------------------------");
        System.out.println("Solved Using H1\nH1 Depth: " + goalNode1.getCost() 
                         + "\nH1 Search Cost: " + goalNode1.getSearchCost()
                         + "\nH1 Time: " + total1 + " ms");
        System.out.println("\nSolved Using H2\nH2 Depth: " + goalNode2.getCost() 
                         + "\nH2 Search Cost: " + goalNode2.getSearchCost()
                         + "\nH2 Time: " + total2 + " ms"+"\n");
        if(goalNode1.getCost() != goalNode2.getCost()){
            System.out.println(goalNode1.getCost() + " != " + goalNode2.getCost());
            System.out.println("The depths calculated from the heursitics are not the same! Exiting!");
            System.exit(0);           
        }
        return new SearchData(goalNode1.getCost(),goalNode1.getSearchCost(),total1,goalNode2.getSearchCost(), total2);
    }

    /**
     * To hold solved data from search data to calculate average time
     */
    private class SearchData {
        public int depth;
        public long totalTimeH1;
        public int searchCostH2;
        public long totalTimeH2;
        public SearchData(int d, int sCostH1, long tTimeH1, int sCostH2, long tTimeH2){
            depth = d;
            totalTimeH1 = tTimeH1;            
            searchCostH2 = sCostH2;
            totalTimeH2 = tTimeH2;
        }  
    }
    
}