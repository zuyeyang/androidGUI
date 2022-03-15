package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import android.text.ClipboardManager;


public class MainActivity extends AppCompatActivity {
    private Button deleteBtn ;
    private Button resetdbBtn ;
    private Button newgameBtn;
    //BENSON FINAL NEW
    private Button exportgameBtn;
    private PopupWindow exportPopupWindow;
    //BENSON FINAL NEW

    private PopupWindow deletePopupWindow;
    private PopupWindow resetdbPopupWindow;
    private PopupWindow newgamePopupWindow;

    private View mainpage ;


    private Spinner gameChoiceSpinner;
    private String gameName;
    private GameSingleton singleton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Boolean firstTime = Game.fileExists(MainActivity.this, "bunnyworldg2023.json");
        firstTime = Game.fileExists(MainActivity.this, "bunnyworldg2023_extension.json");
        if (!firstTime){
            try {
                getRawFile();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        //Set DELETE Button
        deleteBtn = (Button) findViewById(R.id.DELETE);
        resetdbBtn = (Button) findViewById(R.id.RESETDB);
        newgameBtn = (Button) findViewById(R.id.NEWGAME);

        //BENSON FINAL NEW
        exportgameBtn = (Button) findViewById(R.id.EXPORT);
        //BENSON FINAL NEW

        mainpage = (View) findViewById(R.id.mainpage);
        gameChoiceSpinner = (Spinner) findViewById(R.id.gameChoiceSpinner);
        gameName = gameChoiceSpinner.getSelectedItem().toString();
        Log.d("MainActivity","Game "+gameName+" is selected");
        setSpinner();

        // Class Spinner implementing onItemSelectedListener
        gameChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // do something upon option selection
                MainActivity.this.gameName = gameChoiceSpinner.getSelectedItem().toString();

                singleton = GameSingleton.getGameInstance();
                try {
                    singleton.loadGame(MainActivity.this, MainActivity.this.gameName);
                } catch (IOException | JSONException e ) {
                    e.printStackTrace();
                }
                GameView.setGameName(MainActivity.this.gameName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });
        //BENSON_NEW===============================================================

        //Popup
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //instantiate popupmaindelete.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupmaindelete, null);

                Button deleteclosePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
                Button deletedeletePopupBtn = (Button) customView.findViewById(R.id.deletePopupBtn);

                //Start popup window
                deletePopupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                deletePopupWindow.showAtLocation(mainpage, Gravity.CENTER, 0, 0);


                //set the button in popupwindow
                deletedeletePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String deleteGameName = gameChoiceSpinner.getSelectedItem().toString();
                        //Benson 0307 new
                        if (deleteGameName.equalsIgnoreCase("bunnyworldg2023")){
                            Toast toast = Toast.makeText(MainActivity.this, "You can't delete the default game bunnyworldg2023.",Toast.LENGTH_LONG);
                            toast.show();
                            deletePopupWindow.dismiss();
                        } else if (deleteGameName.equalsIgnoreCase("bunnyworldg2023_extension")){
                            Toast toast = Toast.makeText(MainActivity.this, "You can't delete the default game bunnyworldg2023_extension.",Toast.LENGTH_LONG);
                            toast.show();
                            deletePopupWindow.dismiss();
                        }
                        else {
                            Game.deleteGame(MainActivity.this, deleteGameName);
                            setSpinner();
                            deletePopupWindow.dismiss();
                        }
                        //Benson 0307 new
                    }
                });
                deleteclosePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePopupWindow.dismiss();
                    }
                });

            }
        });

        //Popup
        resetdbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("resetDB","instantiate popupmaindresetdb.xml layout file");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupmainresetdb, null);

                Button  resetdbresetdbPopupBtn = (Button) customView.findViewById(R.id.resetdbPopupBtn);
                Button resetdbclosePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);

                //Start popup window
                resetdbPopupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                resetdbPopupWindow.showAtLocation(mainpage, Gravity.CENTER, 0, 0);
                Log.d("resetDB","ending popupmaindresetdb.xml layout file");
                //set the button in popupwindow
                resetdbresetdbPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("resetDB","click the bottom");
                        try {
                            Game.deleteAll(MainActivity.this);

                            getRawFile();

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Log.d("resetDB","Error");
                        }

                        setSpinner();
                        resetdbPopupWindow.dismiss();
                    }
                });
                resetdbclosePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetdbPopupWindow.dismiss();
                    }
                });
            }
        });

        newgameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start popup window

                Log.d("newGame","instantiate newGamepop.xml layout file");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupnewgame, null);

                Button newgameYesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
                Button newgameNoPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);

                //Start popup window
                newgamePopupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                newgamePopupWindow.showAtLocation(mainpage, Gravity.CENTER, 0, 0);
                newgamePopupWindow.setFocusable(true);
                newgamePopupWindow.update();
                Log.d("newGame","ending popupmaindrename.xml layout file");

//BENSON FINAL NEW  (new gmae empty name)
                //set the button in popupwindow
                newgameYesPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("newGame","click the bottom");
                        EditText newGameName;
                        try {
                            newGameName = (EditText) customView.findViewById(R.id.newGameName);
                            String newGameNameString = newGameName.getText().toString();
                            Log.d("newGame", "Name is " + newGameNameString);
                            //BENSON FINAL NEW //BENSON FINAL NEW
//                            Boolean isExisted = Game.fileExists(MainActivity.this, newGameNameString+".json");
                            Boolean isExisted = false;
                            ArrayList<String> gameNamesList = Game.getGameNamesToList(MainActivity.this);
                            for (String i : gameNamesList){
                                if (i.equalsIgnoreCase(newGameNameString)){
                                    isExisted = true;
                                    break;
                                }
                            }
                            Boolean isSpace = false;
                            for (char c : newGameNameString.toCharArray()) {
                                if (Character.isWhitespace(c)) {
                                    isSpace =  true;
                                }
                            }

                            Log.d("fileExists", "newgameYesPopupBtn" + String.valueOf(isExisted));
                            if (isExisted){
                                Toast toast = Toast.makeText(MainActivity.this, "Name existed. Try another one!",Toast.LENGTH_LONG);
                                toast.show();
                                newgamePopupWindow.dismiss();
                            } else if(newGameNameString.equals("")) {
                                Toast toast = Toast.makeText(MainActivity.this, "Game name can't be Empty",Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else if (isSpace){
                                Toast toast = Toast.makeText(MainActivity.this, "Game name can't include whitespace",Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {
                                Game newgame = new Game();
                                Page page1 = new Page("page1");
                                newgame.setCurPage(page1);
                                newgame.addPage(page1);
                                newgame.loadGameintoJSON(MainActivity.this, newGameNameString);

                                singleton = GameSingleton.getGameInstance();
                                try {
                                    singleton.loadGame(MainActivity.this, newGameNameString);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                setSpinner();
                                //TODO: go to edit page
                                newgamePopupWindow.dismiss();
                                Toast toast = Toast.makeText(MainActivity.this, "You successfully created a new game! Select in menu to play or edit!",Toast.LENGTH_LONG);
                                toast.show();
                            }

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Log.d("newGame","Error");
                        }


                    }
                });
                newgameNoPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newgamePopupWindow.dismiss();
                    }
                });
            }
        });
//BENSON FINAL NEW
        exportgameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //instantiate popupexportgame.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupexportgame, null);

                Button exportclosePopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);
                Button exportexportPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);

                //Start popup window
                exportPopupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                exportPopupWindow.showAtLocation(mainpage, Gravity.CENTER, 0, 0);

                ScrollView jsonDisplay = (ScrollView) customView.findViewById(R.id.jsondisplay);
                EditText jsonString = (EditText) customView.findViewById(R.id.jsonString);
                String outputString = Game.getStringFromJSONFile(MainActivity.this, gameName+".json");
                outputString = outputString.replace("}, ", "},\n");
                outputString = outputString.replace(", ", ",\n             ");
                jsonString.setText(outputString);
                //set the button in popupwindow
                exportexportPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //gameName
                        ClipboardManager cm = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(jsonString.getText());
                        Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });
                exportclosePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exportPopupWindow.dismiss();
                    }
                });

            }
        });
    }

    public void onSubmitEditMode(View view) {
        Intent intent = new Intent(this, EditMode.class);
        startActivity(intent);
    }
    public void onSubmitGameMode(View view) {
        Intent intent = new Intent(this, GameMode.class);
        startActivity(intent);
    }
    //BENSON_NEW===============================================================
    private void setSpinner(){
        ArrayList<String> gameNamesList = Game.getGameNamesToList(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.gamename_spinner, gameNamesList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.gamename_spinner);
        gameChoiceSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void getRawFile() throws IOException, JSONException {
        StringBuilder gameStringData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.bunnyworldg2023)));
        Log.d("getRawFile","finish reading default bunnyworldg2023");
        String gameStringLine;
        while ((gameStringLine = reader.readLine()) != null) {
            Log.d("test", gameStringLine);
            gameStringData.append(gameStringLine);
        }
        Log.d("getRawFile","Load the default bunnyworldg2023.json");

        Game temp_game = new Game();
        temp_game.loadDataFromGameString(MainActivity.this,gameStringData.toString());
        temp_game.loadGameintoJSON(MainActivity.this,Game.defaultGameName);


        StringBuilder gameStringData1 = new StringBuilder();
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.bunnyworldg2023_extension)));
        String gameStringLine1;
        while ((gameStringLine1 = reader1.readLine()) != null) {
            Log.d("test", gameStringLine1);
            gameStringData1.append(gameStringLine1);
        }

        Game temp_game1 = new Game();
        temp_game1.loadDataFromGameString(MainActivity.this,gameStringData1.toString());
        temp_game1.loadGameintoJSON(MainActivity.this,Game.defaultGameName1);
    }
    //BENSON_NEW===============================================================




}
