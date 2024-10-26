package p04;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {

        char[][] board = {  {'1', '2', '3'}, 
                            {'4', '5', '6'}, 
                            {'7', '8', '9'} };            
        boolean[] freeSpaces = new boolean[9];
        Arrays.fill(freeSpaces, Boolean.TRUE);

        // // for testing
        // ========================================================================
        // updateBoard(board, Integer.toString(5), true);
        // printBoard(board);
        
        // updateFreeSpaces(freeSpaces, "5");
        // System.out.println(Arrays.toString(freeSpaces));

        // int x = chooseRandomMove(freeSpaces);
        // System.out.println(x);

        // updateBoard(board, Integer.toString(x), false);
        // updateFreeSpaces(freeSpaces, Integer.toString(x));
        // printBoard(board);
        
        // System.out.println(Arrays.toString(freeSpaces));
        // ========================================================================
        
        int port = 3000;
        ServerSocket server = new ServerSocket(port);
        Socket socket = server.accept();
        System.out.printf("connected to client at port %d\n", port);

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        boolean close = false;
        boolean playerTurn = false;
        boolean gameStart = false;
        
        // let player be X and server be O
        while (!close){
            
            if (!gameStart){ // game start
                oos.writeObject("Game start. Flipping coin to decide who goes first. You are X");
                playerTurn = coinFlip();
                gameStart = true;
                if (playerTurn){
                    System.out.println("Player goes first");
                    oos.writeObject("Player goes first");
                }
                else {
                    System.out.println("Computer goes first");
                    oos.writeObject("Computer goes first");
                }
            }
            else  { // game end with draw, win or lose
                int result = gameWon(board);
                boolean draw = gameDraw(freeSpaces, board);
                if (gameEnd(draw, result)){
                    if (gameDraw(freeSpaces, board)){
                        System.out.println("game ended. Draw.");
                        oos.writeObject("game ended. Draw.");
                    }
                    else if (result==1){
                        System.out.println("game ended. Player wins.");
                        oos.writeObject("game ended. Player wins.");
                    }
                    else if (result==-1){
                        System.out.println("game ended. Computer wins.");
                        oos.writeObject("game ended. Computer wins.");
                    }
                    gameStart = false;
                    close = true;
                    break;
                }
            }
            oos.flush();

            String playerInput = "";
            String serverMove = "";
            

            if (playerTurn){
                oos.writeObject("Player's turn.");    
                oos.writeObject(board); // 1. send board
                oos.flush();
                
                playerInput = br.readLine(); // player input is a position on the board that is not taken 
                System.out.println(playerInput);
                if (validMove(freeSpaces, playerInput)){
                    oos.writeObject("Player picked " + playerInput); // 2. send string
                    updateBoard(board, playerInput, playerTurn);
                    updateFreeSpaces(freeSpaces, playerInput);
                    playerTurn = false;

                }
                else{
                    System.out.println("Tile is occupied. Choose unoccupied tile.");
                    oos.writeObject("Tile is occupied. Choose unoccupied tile.");
                }
                oos.flush();
            }
            else {
                oos.writeObject("Computer's turn.");
                serverMove = chooseRandomMove(freeSpaces);
                updateBoard(board, serverMove, playerTurn);
                updateFreeSpaces(freeSpaces, serverMove);
                oos.writeObject("Computer picked " + serverMove); // 1. send string
                oos.flush();
                playerTurn = true;
            }

            // String move = playerTurn? playerInput : serverMove;
            // updateBoard(board, move, playerTurn);
            // updateFreeSpaces(freeSpaces, move);
            // oos.flush();
            
            // playerTurn = playerTurn? false : true;
            printBoard(board); //remove later. nothing wrong with this

            Thread.sleep(2000);
            
        }

    }
    
    public static void printBoard(char[][] board) { // copy from stack overflow
        System.out.println();
        System.out.println("Choose position below not marked by 'X' or 'O' ");
        System.out.println();
        System.out.println("  ┌───┬───┬───┐");
        System.out.println("  │ "
                + board[0][0] + " │ "
                + board[0][1] + " │ "
                + board[0][2] + " │ ");
        System.out.println("  ├───┼───┼───┤");
        System.out.println("  │ "
                + board[1][0] + " │ "
                + board[1][1] + " │ "
                + board[1][2] + " │ ");
        System.out.println("  ├───┼───┼───┤");
        System.out.println("  │ "
                + board[2][0] + " │ "
                + board[2][1] + " │ "
                + board[2][2] + " │ ");
        System.out.println("  └───┴───┴───┘");
    }

    public static boolean coinFlip(){
        Random r = new Random();
        return r.nextBoolean();
    }

    public static boolean validMove(boolean[] freeSpaces, String playerInput){
        return (freeSpaces[Integer.parseInt(playerInput)-1]? true : false);
    }

    public static void updateBoard(char[][] board, String move, boolean playerTurn){
        int row = (Integer.parseInt(move)-1)/3;
        int col = (Integer.parseInt(move)-1)%3;

        board[row][col] = playerTurn? 'X' : 'O';
    }

    public static void updateFreeSpaces(boolean[] freeSpaces, String move){
        freeSpaces[Integer.parseInt(move)-1] = false;
    }

    public static String chooseRandomMove(boolean[] freeSpaces){ 
        Random r = new Random();
        List<Integer> freeIndex = new ArrayList<>();
        for (int i=0 ;i < freeSpaces.length ; i++){
            if (freeSpaces[i]){
                freeIndex.add(i);
            }
        }
        return Integer.toString(freeIndex.get(r.nextInt(freeIndex.size()))+1);
    }

    public static int gameWon(char[][] board){
        // check rows
        if (board[0][0] == board[0][1] && board[0][1]==board[0][2] && board[0][2]=='X'){
            return 1;
        }
        else if (board[0][0] == board[0][1] && board[0][1]==board[0][2] && board[0][2]=='O'){
            return -1;
        }
        else if (board[1][0] == board[1][1] && board[1][1]==board[1][2] && board[1][2]=='X'){
            return 1;
        }
        else if (board[1][0] == board[1][1] && board[1][1]==board[1][2] && board[1][2]=='O'){
            return -1;
        }
        else if (board[2][0] == board[2][1] && board[2][1]==board[2][2] && board[2][2]=='X'){
            return 1;
        }
        else if (board[2][0] == board[2][1] && board[2][1]==board[2][2] && board[2][2]=='O'){
            return -1;
        }
        // check col
        else if (board[0][0] == board[1][0] && board[1][0]==board[2][0] && board[2][0]=='X'){
            return 1;
        }
        else if (board[0][0] == board[1][0] && board[1][0]==board[2][0] && board[2][0]=='O'){
            return -1;
        }
        else if (board[0][1] == board[1][1] && board[1][1]==board[2][1] && board[2][1]=='X'){
            return 1;
        }
        else if (board[0][1] == board[1][1] && board[1][1]==board[2][1] && board[2][1]=='O'){
            return -1;
        }
        else if (board[0][2] == board[1][2] && board[1][2]==board[2][2] && board[2][2]=='X'){
            return 1;
        }
        else if (board[0][2] == board[1][2] && board[1][2]==board[2][2] && board[2][2]=='O'){
            return -1;
        }
        // check diagonal
        else if (board[0][0] == board[1][1] && board[1][1]==board[2][2] && board[2][2]=='X'){
            return 1;
        }
        else if (board[0][0] == board[1][1] && board[1][1]==board[2][2] && board[2][2]=='O'){
            return -1;
        }
        else if (board[0][2] == board[1][1] && board[1][1]==board[2][0] && board[2][0]=='X'){
            return 1;
        }
        else if (board[0][2] == board[1][1] && board[1][1]==board[2][0] && board[2][0]=='O'){
            return -1;
        }
        else {
            return 0;
        }
    }

    public static boolean gameDraw(boolean[]freeSpaces, char[][] board){
        int numOfFreeSpaces = 0;
        for (boolean b : freeSpaces){
            numOfFreeSpaces = b? numOfFreeSpaces+1 : numOfFreeSpaces;
        }
        return ((numOfFreeSpaces==0 && gameWon(board)==0)? true : false);
    }

    public static boolean gameEnd(boolean draw, int result){
        return ((draw || result==1 || result==-1)? true : false);
    }
}

