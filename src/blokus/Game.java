package blokus;

import ai.*;
import gui.MainWindow;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import BlokusMain;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public class Game implements java.io.Serializable {
    private Board _board;
    private Color current_turn;
    private LinkedList<Player> _players;
    private int num_of_consecutive_passes;
    private transient MainWindow _gui;

    //always instantiate the engine, even if there are no ai players, so we can
    //check if there are no moves left, and to rate moves.
    private AIEngine ai_engine;
    
    public Game() {
        this(null);
    }

    public Game(MainWindow gui) {
        _board = new Board();
        current_turn = Color.BLUE; //blue is first
        _players = new LinkedList<Player>();
        num_of_consecutive_passes = 0;
        _gui = gui;
    }

    /**
     * Add a player to this game. The color of the player will be set by this
     * constructor.
     * @param newPlayer
     * @return
     */
    public boolean addPlayer(Player newPlayer) {
        //set to null b/c otherwise compiler complains about initialization
        Color newColor = null;

        //get the 'next' color
        //return false if there are already 4 players playing
        switch (_players.size()) {
            case 0: newColor = Color.BLUE; break;
            case 1: newColor = Color.YELLOW; break;
            case 2: newColor = Color.RED; break;
            case 3: newColor = Color.GREEN;
                    //set AI engine
                    this.setAIEngine();
                    break;
            case 4: return false;
        }

        newPlayer.setColor(newColor);
        _players.add(newPlayer);

        return true;
    }

    //**NOT USED FOR ANYTHING EXCEPT COMMAND LINE**
    /**
     * Gets the next move from the player whose turn it is and then makes it
     * (sends it to the board).
     * @return Returns whether there have been 4 consecutive passes, and thus
     * the game is over.
     */
    public boolean nextMove() {
        //find current player. a little slow, player could be stored to speed up
        Player currentPlayer = null;
        for (Player player : _players) {
            if (player.getColor() == current_turn) {
                currentPlayer = player;
            }
        }
        if (currentPlayer == null) {
            System.out.println("Serious error in Game.nextMove: player was null.");
            System.exit(0);
        }

        Move move = null;
        MoveMessage message;
        try{
            do {
                //this hangs until the player returns a move
                //for humans this is until they make a move in the GUI
                //for AI this is until we have computed the best move
                //for network this is until we get the move from the network
                move = currentPlayer.getMove();
                message = _board.isValidMessage(move);
            } while (message != MoveMessage.SUCCESS);
        }
        catch(Exception e) {
            
        }
        return nextMove(move);
    }

    /**
     * This is called by the network thread to update the board and the game.
     * @param move - move returned from network
     * @return Returns whether there have been 4 consecutive passes, or no one
     * has pieces, and thus the game is over.
     */
    public boolean nextMove(Move move){
        //ai engine should never be null
        ai_engine.registerMove(move);

        if (!move.isPass()) {
            _board.setMove(move);
            for(int i = 0; i< _players.size(); i++){
                if(_players.get(i).getColor()==move.getColor()){
                    _players.get(i).removePiece(move.getShape());
                }
            }
            num_of_consecutive_passes = 0;
        }
        else
            num_of_consecutive_passes++;

        boolean noOneHasPieces = true;
        for (Player player : _players) {
            if (player.getPieces().size() > 0) {
                noOneHasPieces = false;
            }
        }

        if (num_of_consecutive_passes == 4 || 
                noOneHasPieces) {
            return false;
        }

        //advance current turn
        switch (current_turn) {
            case BLUE: current_turn = Color.YELLOW; break;
            case YELLOW: current_turn= Color.RED; break;
            case RED: current_turn = Color.GREEN; break;
            case GREEN: current_turn = Color.BLUE; break;
        }
        return true;
    }

    /**
     * Gets the evaluation of the current board for the player with the
     * specified color. In other words, gets the rating of the move just made by
     * this player.
     * @param color Color of the player to evaluate
     * @return Rating of the current board for the player
     */
    public double getMoveRating(Color color) {
        double[] rn = ai_engine.getBoardRating();
        double sum = 0;
        for (double r : rn) {
            sum += r;
        }
        return rn[color.ordinal()]/sum;
    }

    /**
     * @return Whether or not the current player has any available moves
     */
    public boolean hasNoMoves() {
        return ai_engine.hasNoMoves();
    }

    /**
     * If you load a Game object in from disk, you MUST set this, because
     * we do not save it in the file. This also updates all the human players
     * in the game with the gui
     * @param gui The MainWindow to use
     */
    public void setGUI(MainWindow gui) {
        _gui = gui;
        for (Player player : _players) {
            if (player instanceof HumanPlayer) {
                ((HumanPlayer) player).setGUI(gui);
            }
        }
    }

    /**
     * Returns an auto-generated name for a saved game, which is the current
     * date and time + .blok (the extension is saved in constants)
     * For example, if it is April 5, 2010, at 14:23:48, the file would be"
     * 04-05-2010--14:23:48.blok
     * @return The auto-generated fileName for a saved game
     */
    public String generateSaveGameName() {
        //create the saved games dir if it doesn't exist
        new File(Constants.savedGamesDir).mkdir();

        //create file path and name
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy--HH:mm:ss");
        String dateAndTime = sdf.format(cal.getTime());
        String fileName = dateAndTime + Constants.savedGamesExt;
        
        return fileName;
    }

    /**
     * Saves a game in Constants.savedGamesDir with the specified filename.
     * @param fileName Name of saved game
     * @return filename of the saved game
     */
    public void saveGame(String fileName) {
        String filePath = Constants.savedGamesDir + fileName;
        
        //save the game
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filePath);
            out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(BlokusMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads a game with the given filename in. The file must be in the same
     * directory as the project.
     * @param fileName The name of the game to be loaded in
     */
    public Game loadGame(String fileName) {
        String filePath = Constants.savedGamesDir + fileName;

        Game loadedGame = null;
        
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(filePath);
            in = new ObjectInputStream(fis);
            loadedGame = (Game) in.readObject();
            in.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BlokusMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BlokusMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadedGame.setGUI(_gui);
        loadedGame.setAIEngine();
        
        return loadedGame;
    }

    private void setAIEngine() {
        boolean hasAIPlayer = false;
        for (Player player : _players) {
            if (player instanceof AIPlayer) {
                hasAIPlayer = true;
                ai_engine = ( (AIPlayer) player).getEngine();
                break;
            }
        }

        if (!hasAIPlayer) {
            ai_engine = AIEngine.getSingleton();
        }

        //If making a new game from a game, must reset AI engine
        ai_engine.reset();
        
    }

    //for testing
    protected int getNumOfPlayers() {
        return _players.size();
    }

    public Board getBoard() {
        return _board;
    }

    /**
     * Network needs this to find out the current turn.
     * @return
     */
    public Color getCurrentTurn() {
        return current_turn;
    }

    public LinkedList<Player> getPlayers(){
        return _players;
    }
}
