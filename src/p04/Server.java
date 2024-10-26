package p04;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException {

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
                if (gameDraw(freeSpaces, board)){
                    System.out.println("game ended. Draw.");
                    oos.writeObject("game ended. Draw.");
                }
                else if (gameWon(board)==1){
                    System.out.println("game ended. Player wins.");
                    oos.writeObject("game ended. Player wins.");
                }
                else if (gameWon(board)==-1){
                    System.out.println("game ended. Computer wins.");
                    oos.writeObject("game ended. Computer wins.");
                }
                gameStart = false;
                close = true;
                break;
            }
            
            if (playerTurn){
                String playerInput = br.readLine(); // player input is a position on the board that is not taken 

                if (validMove(freeSpaces, playerInput)){
                    updateBoard(board, playerInput, playerTurn);
                    updateFreeSpaces(freeSpaces, playerInput);
                    oos.writeObject(board);
                    playerTurn = false;
                }
                else{
                    System.out.println("Choose unoccupied tile.");
                    oos.writeObject("Choose unoccupied tile.");
                }
            }
            else {
                int serverMove = chooseRandomMove(freeSpaces);
                updateBoard(board, Integer.toString(serverMove), playerTurn);
                updateFreeSpaces(freeSpaces, Integer.toString(serverMove));
                oos.writeObject(board);
                playerTurn = true;
            }
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
        if (freeSpaces[Integer.parseInt(playerInput)-1]){
            return true;
        }
        return false;
    }

    public static void updateBoard(char[][] board, String move, boolean playerTurn){
        int row = (Integer.parseInt(move)-1)/3;
        int col = (Integer.parseInt(move)-1)%3;

        if (playerTurn){
            board[row][col] = 'X';
        }
        else {
            board[row][col] = 'O';
        }
    }

    public static void updateFreeSpaces(boolean[] freeSpaces, String move){
        freeSpaces[Integer.parseInt(move)-1] = false;
    }

    public static int chooseRandomMove(boolean[] freeSpaces){ 
        Random r = new Random();
        List<Integer> freeIndex = new ArrayList<>();
        for (int i=0 ;i < freeSpaces.length ; i++){
            if (freeSpaces[i]){
                freeIndex.add(i);
            }
        }
        System.out.println(freeIndex);
        return freeIndex.get(r.nextInt(freeIndex.size()))+1;
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
        // boolean someoneWon = gameWon(board);
        int numOfFreeSpaces = 0;
        for (boolean b : freeSpaces){
            if (b){
                numOfFreeSpaces += 1;
            }
        }
        if (numOfFreeSpaces==0 && gameWon(board)==0){
            return true;
        }
        return false;
    }

    public static int gameEnd(boolean draw, int result){
        if (draw) return 0;
        else return result;
    }

    
}

