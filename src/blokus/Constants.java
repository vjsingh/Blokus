/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package blokus;

import java.io.File;

/**
 *
 * @author ahills, dkimmel, guyoung, vjsingh
 */
public interface Constants {
    static final int BOARD_SIZE = 9;
    static final String savedGamesDir = "." + File.separator + 
            "savedGames" + File.separator;
    static final String savedGamesExt = ".blok";
    static final int AIDifficulty = 1;
}
