/*
 * Implementing classes: Server, Client
 */

package network;

import blokus.Move;
import blokus.Color;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public interface NetworkComponent {

    /**
     * This method completes the given player's turn and returns the move
     * that was made. Server and Client implement this differently: Server
     * receives the move and sends it to everyone. Client either receives the
     * move from the server for all other players or sends a move for its own
     * player and awaits the same move in response.
     *
     * @param playernum - something representing which player's turn
     * @return - move made by that player
     */
    public Move takeTurn(Color current) throws NetworkException;

    public void close();

}
