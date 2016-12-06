import Game.GameWindow;

import java.io.IOException;

/**
 * Created by Hoangelato on 01/11/2016.
 */
public class main {
    public static void main(String[] args) throws IOException {
        GameWindow gameWindow = new GameWindow();
        Thread thread = new Thread(gameWindow);
        thread.start();
    }
}
