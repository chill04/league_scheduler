import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
// import java.util.*; 

public class scheduler {
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        // Scanner input = new Scanner(System.in);

        int playerCount = playerCount();  
        String[] names = createPlayerNames(playerCount);
        schedule(names);

        // input.close();   // The Scanner is used repeatedly, so it is finally closed at the very end of the program
    }


    public static int playerCount() {
        int playerCount = 0;
        boolean isValid = false;

        while(!isValid){
            System.out.print("Enter number of players: ");
            
            if (input.hasNextInt()){
                playerCount = input.nextInt();
                isValid = true;
//                input.close();
            } else {
                input.next();
                System.out.println("That's not a number.");
            }       
        }
    return playerCount;
    }
    
    public static String[] createPlayerNames(int playerCount){
        // Hashtable<Integer, String> playerIDs = new Hashtable<Integer,String>();
        String[] names = new String[playerCount];
        boolean isValid;
        for(int counter = 1; counter <= names.length; counter++){
            isValid = false;

            while (!isValid){
                System.out.print("Enter Player " + counter + " name:");
                if (input.hasNext()){
		        	names[counter - 1] = input.next();
		            	isValid = true;
		        }
		        else {
		        	input.next();
		        };
//                }
//                catch(IllegalStateException ex) {
//                	
//                } 
            }
        }
        for(int counter = 1; counter <= names.length; counter++){
            System.out.print("PLAYER " + counter + ": " + names[counter -1] + "\n");
        }
    return names;
    } 

    public static String[] addByeWeek(String[] names) {
        String[] byeWeek = new String[names.length + 1];      // So let's check for that and create a "BYE WEEK" that each player will have on their schedule.  
        for (int i = 0; i<names.length; i++){                 
            byeWeek[i] = names[i];                           // Java can't append to arrays, so this creates a new array and copies the old one over.
        }
        byeWeek[names.length] = "BYE";
        return byeWeek;
    }

    public static int askWeeks(String[] names){
        //  If there's four teams, they can play in 3 weeks.  If there's 3 teams, they can play in 3 weeks.
        //  But, since all odd numbers are rounded up by the addByeWeek() method, they can be handled in the same way.  
        int weeks = names.length -1; 
        
        boolean isValid = false;                            
        while(!isValid){
            System.out.print("How many rounds of games?  " + weeks + " is default, for everyone to play each other once.");
            if (input.hasNextInt()){
                weeks = input.nextInt();
                isValid = true;
            } else {
                input.next();
                System.out.println("That's not a number.");
            }       
        }
        return weeks;
    } 

    public static void schedule(String[] names) {

        if (names.length % 2 !=0){      // If there's an odd number of players, then not everyone will be able to play each round.  A player named "BYE" is created to represent the non-game.  
            names = addByeWeek(names);
        }

        int weeks = askWeeks(names);   // Independent method to get user input about how many weeks should be played.  


        int rows = names.length +1;            // Make an extra row for the header
        int columns = weeks +1;
        String[][] masterSchedule= new String[rows][columns];      // Schedule is [PLAYER][WEEK]
        masterSchedule[0][0] = "-";


        for ( int teamCounter = 0; teamCounter < names.length; teamCounter++){      //This loop cycles through each player
            
            int opponentCounter = 9000;                               // placeholder integer that cannot be zero
            masterSchedule[teamCounter+1][0] = names[teamCounter];   // Add team name to column 1
            

            for ( int weekCounter = 0; weekCounter < weeks; weekCounter++){        //loop through each week
                masterSchedule[0][weekCounter + 1] = "Week " + (weekCounter +1);
                if (weekCounter == 0){
                    opponentCounter = teamCounter;                                //Each team's schedule begins at their own index
                }


                //  To create schedules, players with an odd index increment through the list (player1 plays player 2, then player 3)
                 if (teamCounter%2 != 0){            
                opponentCounter++;
                    if (teamCounter == opponentCounter){   // Cannot play yourself, so skip over that index.
                        opponentCounter++;
                    }

                //  and players with an even index decrement through the list (player 2 will play player 1, then player0)
                }  else {
                    opponentCounter--;                   
                    if (teamCounter == opponentCounter){
                        opponentCounter--;              // Cannot play yourself; skip that index.
                    }
                }

                //when the loop gets to the bottom of the list, it will jump to the top.  (ex: play team 4, then team 0, then team 1, etc)
                if (opponentCounter < 0){
                    opponentCounter += names.length;     
                }
                //or when it gets to the top of the list, it should jump to the bottom. 
                if (opponentCounter >= names.length){      
                    opponentCounter = 0;
                }

            // with the player, opponent, and week identified; add them together to the array.
            masterSchedule[teamCounter+1][weekCounter+1] = names[teamCounter].toUpperCase() + " vs. " + names[opponentCounter].toUpperCase();
            }
        }
        // When all loops are complete, print the completed schedule.  
        for (int i = 0; i < rows; i++) {
            if (masterSchedule[i][0] != "BYE"){                         // The bye week was a placeholder and doesn't need it's schedule printed.
            for (int j = 0; j < columns; j++) {
                System.out.format("%18s", masterSchedule[i][j]+ " ");
            }
            System.out.print("\n");
            }
        }
        
        offerCSV(masterSchedule);
    }


    
    public static void offerCSV(String[][] masterSchedule) {
        boolean isValid = false;            
        while (!isValid){
            System.out.print("Schedule may be hard to read on terminal.  Output schedule to spreadsheet? (Y/N):");
            if (input.hasNext()){
                String outputResponse = input.next();
                if (outputResponse.toUpperCase().contains("Y")){
                    writeCSV(masterSchedule);
                } 
                isValid = true;
            }
        }
    }



    public static void writeCSV(String[][] schedule) {
        int rows = schedule.length;
        int columns = schedule[0].length;
        File output = new File("output.csv");
        for (int i = 0; i < rows; i++) {
            if (schedule[i][0] != "BYE"){
                String teamRow = "";   
                
                for (int j = 0; j < columns; j++) {
                    teamRow = teamRow + schedule[i][j]+ ",";
                }
                teamRow = teamRow + "\n";
                try{
                Files.writeString(output.toPath(), teamRow, StandardOpenOption.APPEND);
                }
                catch(IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } 
            }
        }
    }
}