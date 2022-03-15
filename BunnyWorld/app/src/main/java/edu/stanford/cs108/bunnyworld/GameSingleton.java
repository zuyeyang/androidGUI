package edu.stanford.cs108.bunnyworld;

import android.content.Context;

import org.json.JSONException;
import java.io.IOException;

public class GameSingleton {
    private static final GameSingleton GameInstance = new GameSingleton();

    public static GameSingleton getGameInstance() { return GameInstance; }

    private GameSingleton() {}

    private final Game game = new Game();

    public void loadGame(Context context, String gameName) throws IOException, JSONException {
        game.loadJSONIntoGame(context, gameName);
    }

    public Game getGame() { return game; }

    private Shape selectedShape;
    private Shape copiedShape;

    public Shape getSelectedShape(){
        return selectedShape;
    }

    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }

    public Shape getCopiedShape(){
        return copiedShape;
    }

    public void setCopiedShape(Shape shape){
        this.copiedShape  = shape;
    }
}
