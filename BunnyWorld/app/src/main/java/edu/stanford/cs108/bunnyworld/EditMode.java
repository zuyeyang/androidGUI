package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class EditMode extends AppCompatActivity {

    private GameSingleton currGameSingleton = GameSingleton.getGameInstance();
    private Game currGame;

    private View editPage;
    private View selectShapeView;
    private View setScriptView;
    private PopupWindow selectShape;
    private PopupWindow setScript;
    private StringBuilder currScript;

    private String shapeName;
    private EditView editview;
    private float viewWidth, viewHeight;

    private Button createPageBtn;
    private Button changePageBtn;
    private Button renamePageBtn;
    private Button clearPageBtn;
    private Button deletePageBtn;
    private Button saveGameBtn;
    private PopupWindow clearPagePopupWindow;
    private PopupWindow deletePagePopupWindow;
    private PopupWindow changePagePopupWindow;
    private PopupWindow createPagePopupWindow;
    private PopupWindow renamePagePopupWindow;
    private TextView curPageName;
    private Page curPage;
    //    Page firstPage;
    private TextView currGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);

        editPage = findViewById(R.id.editPage);
        editview = (EditView) findViewById(R.id.editView);
        editPage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                viewHeight = editPage.getHeight() * 0.75f;
                viewWidth = editPage.getWidth();
                editview.setViewDimension(viewWidth, viewHeight);
            }
        });
        currScript = new StringBuilder();

        //set page button
        createPageBtn = (Button) findViewById(R.id.CREATEPAGE);
        changePageBtn = (Button) findViewById(R.id.CHANGEPAGE);
        renamePageBtn = (Button) findViewById(R.id.RENAMEPAGE);
        clearPageBtn = (Button) findViewById(R.id.CLEARPAGE);
        deletePageBtn = (Button) findViewById(R.id.DELETEPAGE);
        saveGameBtn = (Button) findViewById(R.id.SAVEGAME);

        editPage = (View) findViewById(R.id.editPage);
        curPageName = (TextView) findViewById(R.id.currPageName);


        //Todo: double check
        Context context = this.getApplicationContext();
        currGame = GameSingleton.getGameInstance().getGame();
        currGame.setCurPage(currGame.getPageList().get(0));
        curPage = GameSingleton.getGameInstance().getGame().getCurPage();
//        firstPage = GameSingleton.getGameInstance().getFirstPage();
//        pageList = GameSingleton.getGameInstance().getGame().pageList;

//        game = new Game();
//        try {
//            game.loadJSONIntoGame(context,"ABensonTestGame");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        pageList = new ArrayList<>();
//        Page p1 = new Page("pageOne");
//        pageList.add(p1);
//        Page p2 = new Page("pageTwo");
//        pageList.add(p2);
//        game = new Game(pageList);
//        game.setCurPage(p1);
//        curPage = game.getCurPage();
//        pageList = game.getPageList();


        Log.d("editModeOnCreate",update(currGame.getCurPage().getName()));
        curPageName.setText("Page: "+update(currGame.getCurPage().getName()));
        //Benson
        currGameName = (TextView) findViewById(R.id.currGameName);
        Log.d("editMode","the current game is "+ currGame.getcurrGameName());
        currGameName.setText("Game: "+update(currGame.getcurrGameName()));
        //Benson

        clearPageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //instantiate popupmaindelete.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) EditMode.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupclearpage, null);

                Button yesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
                Button noPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);

                //Start popup window
                clearPagePopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                clearPagePopupWindow.setElevation(20);
                clearPagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);

                clearPagePopupWindow.setFocusable(true);
                clearPagePopupWindow.update();

                //set the button in popupwindow
                yesPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currGame.getCurPage().getShapeList().size() == 0){
                            System.out.println("empty page");
                            Toast toast = Toast.makeText(context, "This page is already empty!",Toast.LENGTH_LONG);
                            toast.show();
                        }else{
                            currGame.getCurPage().setShapeList(new ArrayList<Shape>());
                            GameSingleton.getGameInstance().setSelectedShape(null);
                            editview.invalidate();
                            System.out.println("page cleared");
                        }
//                        game.getCurPage().setShapeList(new ArrayList<Shape>());
                        clearPagePopupWindow.dismiss();
                    }
                });
                noPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearPagePopupWindow.dismiss();
                    }
                });

                //TODO: need to get the curPage and then delete the shapeList of it;
            }
        });

        deletePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //instantiate popupmaindelete.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) EditMode.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupdeletepage, null);

                Button yesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
                Button noPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);

                //Start popup window
                deletePagePopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                deletePagePopupWindow.setElevation(20);
                deletePagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                deletePagePopupWindow.setFocusable(true);
                deletePagePopupWindow.update();

                //set the button in popupwindow
                yesPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currGame.getPageList().size() == 1){
                            System.out.println("cannot set");
                            Toast toast = Toast.makeText(context, "Only one page left, you cannot delete it!",Toast.LENGTH_LONG);
                            toast.show();
                            currGame.setCurPage(currGame.getPageList().get(0));
                            String name = currGame.getCurPage().getName();
                            name = update(name);
                            curPageName.setText("Page: "+name);
                        }else{
                            if(currGame.getCurPage() == currGame.getPageList().get(0)){
                                Toast toast = Toast.makeText(context, "You cannot delete this default page!",Toast.LENGTH_LONG);
                                toast.show();
                            }else{
                                currGame.getPageList().remove(curPage);
                                currGame.setCurPage(currGame.getPageList().get(0));

                                GameSingleton.getGameInstance().setSelectedShape(null);
                                editview.invalidate();

                                String name = currGame.getCurPage().getName();
                                name = update(name);
                                curPageName.setText("Page: "+name);
                            }
                        }
                        System.out.println("delete curPage");
                        deletePagePopupWindow.dismiss();
                    }
                });
                noPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePagePopupWindow.dismiss();
                    }
                });

                //Todo: need to get the curPage and then delete it from the PageList
            }
        });

        changePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //instantiate popupmaindelete.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) EditMode.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popupchangepage, null);

                Button yesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
                Button noPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);

                //Start popup window
                changePagePopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                changePagePopupWindow.setElevation(20);
                changePagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                changePagePopupWindow.setFocusable(true);
                changePagePopupWindow.update();


                //int n = pageList.size();
                int n = currGame.getPageList().size();
                RadioGroup changePageRadioGroup = (RadioGroup) customView.findViewById(R.id.pageGroup);
                System.out.println("change page");
                RadioButton button;
                for(int i = 0; i < n; i++){
                    button = new RadioButton(customView.getContext());
                    Page page = currGame.getPageList().get(i);
                    String name = page.getName();
                    if(name.length() > 30){
                        name = name.substring(0, 27)+"...";
                    }
                    button.setText(name);
                    button.setId(i);
                    if(page.getIsCurPage()){
                        button.setChecked(true);
                    }
                    changePageRadioGroup.addView(button);
                    System.out.println("page"+i);
                }
                //set the button in popupwindow
                yesPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = changePageRadioGroup.getCheckedRadioButtonId();
                        currGame.setCurPage(currGame.getPageList().get(i));
                        String name = currGame.getCurPage().getName();
                        name = update(name);
                        curPageName.setText("Page: "+name);
                        curPage = currGame.getCurPage();

                        if (GameSingleton.getGameInstance().getSelectedShape() != null) {
                            GameSingleton.getGameInstance().getSelectedShape().setSelected(false);
                        }
                        GameSingleton.getGameInstance().setSelectedShape(null);
                        editview.invalidate();

                        changePagePopupWindow.dismiss();
                    }
                });
                noPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePagePopupWindow.dismiss();
                    }
                });

                //Todo: need to get the number of the PageList and then display the RadioButton according to this number
                //Todo: Also need to change the CurPage when the yesButton is Clicked
            }
        });

        createPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "Page"+(currGame.getPageList().size()+1);
                while(isRepeated(name)){
                    name = name+"_1";
                }
                Page newPage = new Page(name);
                currGame.addPage(newPage);
                currGame.setCurPage(name);

                if (GameSingleton.getGameInstance().getSelectedShape() != null) {
                    GameSingleton.getGameInstance().getSelectedShape().setSelected(false);
                }
                GameSingleton.getGameInstance().setSelectedShape(null);
                editview.invalidate();

                curPage = currGame.getCurPage();
                String displayName = update(name);
                curPageName.setText("Page: "+displayName);

                //instantiate popupmaindelete.xml layout file
//                LayoutInflater layoutInflater = (LayoutInflater) EditMode.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView = layoutInflater.inflate(R.layout.popupcreatepage, null);
//                System.out.println("create clicked");
//
//                Button yesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
//                Button noPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);
//                EditText nameText = (EditText) customView.findViewById(R.id.newPageName);
//
//                InputFilter spaceFilter = (source, start, end, dest, dstart, dend) -> {
//                    if (source.equals(" ")) {return "";}
//                    else { return null; }
//                };
//
//                nameText.setFilters(new InputFilter[]{spaceFilter});
//
//                //Start popup window
//                createPagePopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                createPagePopupWindow.setElevation(20);
//                createPagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
//                createPagePopupWindow.setFocusable(true);
//                createPagePopupWindow.update();
//
//                //set the button in popupwindow
//                yesPopupBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = nameText.getText().toString();
//
//                        if(name.length() > 0){
////                            System.out.println(name);
//                            if(currGame.getPage(name) != null){
//                                Toast toast = Toast.makeText(context,"This page has already existed", Toast.LENGTH_LONG);
//                                toast.show();
//                            }else{
//                                Page newPage = new Page(name);
//                                currGame.addPage(newPage);
//                                currGame.setCurPage(name);
//
//                                GameSingleton.getGameInstance().setSelectedShape(null);
//                                editview.invalidate();
//
//                                curPage = currGame.getCurPage();
//                                name = update(name);
//                                curPageName.setText("Page: "+name);
////                            curPageName.setText(game.getCurPage().getName());
////                        System.out.println(game.getCurPage().getName());
//                                System.out.println(currGame.getPageList().size());
//                            }
//                        }
//                        createPagePopupWindow.dismiss();
//                    }
//                });
//                noPopupBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        createPagePopupWindow.dismiss();
//                    }
//                });
            }

            //Todo: need to create a new page according to the given new Pagename and then add it to the pageList
            //Todo: if enter is clicked, then curPage will be this new page
        });

        renamePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //instantiate popupmaindelete.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) EditMode.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popuprenamepage, null);
                System.out.println("rename clicked");

                Button yesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
                Button noPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);
                EditText renameText = (EditText) customView.findViewById(R.id.newPageName);
                renameText.setHint(currGame.getCurPage().getName());
                renameText.setText(currGame.getCurPage().getName());

                InputFilter spaceFilter = (source, start, end, dest, dstart, dend) -> {
                    if (source.equals(" ")) {return "";}
                    else { return null; }
                };

                renameText.setFilters(new InputFilter[]{spaceFilter});

                //Start popup window
                renamePagePopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                renamePagePopupWindow.setElevation(20);
                renamePagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                renamePagePopupWindow.setFocusable(true);
                renamePagePopupWindow.update();

                //set the button in popupwindow
                yesPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = renameText.getText().toString();
                        if (name.length() > 0){
                            if(isRepeated(name)){
                                Toast toast = Toast.makeText(context,"This page has already existed", Toast.LENGTH_LONG);
                                toast.show();
                            }else{
                                currGame.getCurPage().setPageName(name);
                                String displayName = update(name);
                                curPageName.setText("Page: "+displayName);
                            }
//                            if(currGame.getPage(name) != null){
//                                Toast toast = Toast.makeText(context,"This page has already existed", Toast.LENGTH_LONG);
//                                toast.show();
//                            }else{
//                                currGame.getCurPage().setPageName(name);
//                                name = update(name);
//                                curPageName.setText("Page: "+name);
//                            }
                        }
                        renamePagePopupWindow.dismiss();
                    }
                });
                noPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        renamePagePopupWindow.dismiss();
                    }
                });
            }

            //Todo: need to rename curPage when the curPage is not null
        });
//        renamePageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //instantiate popupmaindelete.xml layout file
//                LayoutInflater layoutInflater = (LayoutInflater) EditMode.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView = layoutInflater.inflate(R.layout.popuprenamepage, null);
//                System.out.println("rename clicked");
//
//                Button yesPopupBtn = (Button) customView.findViewById(R.id.yesPopupBtn);
//                Button noPopupBtn = (Button) customView.findViewById(R.id.noPopupBtn);
//                EditText renameText = (EditText) customView.findViewById(R.id.newPageName);
//                renameText.setHint(currGame.getCurPage().getName());
//                renameText.setText(currGame.getCurPage().getName());
//
//                InputFilter spaceFilter = (source, start, end, dest, dstart, dend) -> {
//                    if (source.equals(" ")) {return "";}
//                    else { return null; }
//                };
//
//                renameText.setFilters(new InputFilter[]{spaceFilter});
//
//                //Start popup window
//                renamePagePopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                renamePagePopupWindow.setElevation(20);
//                renamePagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
//                renamePagePopupWindow.setFocusable(true);
//                renamePagePopupWindow.update();
//
//                //set the button in popupwindow
//                yesPopupBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = renameText.getText().toString();
//                        if (name.length() > 0){
//                            if(currGame.getPage(name) != null){
//                                Toast toast = Toast.makeText(context,"This page has already existed", Toast.LENGTH_LONG);
//                                toast.show();
//                            }else{
//                                currGame.getCurPage().setPageName(name);
//                                name = update(name);
//                                curPageName.setText("Page: "+name);
//                            }
//                        }
//                        renamePagePopupWindow.dismiss();
//                    }
//                });
//                noPopupBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        renamePagePopupWindow.dismiss();
//                    }
//                });
//            }
//
//            //Todo: need to rename curPage when the curPage is not null
//        });

        saveGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final new
                if (!scriptReferCheck()) return;

                try {
                    //lazy fix bug
                    currGame.getCurPage().getInventory().clear();
                    //lazy fix bug
                    currGame.loadGameintoJSON(EditMode.this, currGame.getcurrGameName());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                //Toast
                String name_added = "The game " + currGame.getcurrGameName()+" has been saved";
                Toast toast = Toast.makeText(
                        EditMode.this,
                        name_added,
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }

        });

        //TODO: test only, to be deleted
//        currGame = new Game();
//        Page testPage = new Page("test", currGame);
//        Page testPage2 = new Page("test2", currGame);
//        Shape testShape = new Shape("text", 250, 100, 100, 100,
//                this, testPage);
//        Shape testShape2 = new Shape("carrot", 100, 100, 100, 100,
//                this, testPage);
//        Shape testShape3 = new Shape("greyBox", 100, 100, 100, 100,
//                this, testPage2);
//        Shape testShape4 = new Shape("rect", 100, 100, 100, 100,
//                this, testPage);
//        testShape2.setImgName("carrot");
//        //testShape.parseScript("on click play evillaugh;");
//        testShape.parseScript("on click goto test2;");
//        testShape2.parseScript("on drop text play evillaugh;");
//        testShape2.parseScript("on drop rect play evillaugh;");
//        testShape.setText("no leetcode then dog");
//        testShape3.parseScript("on click goto test;");
//        testShape3.parseScript("on enter play evillaugh;");
//        testShape.setFontSize(100f);
//        testPage.addShape(testShape);
//        testPage.addShape(testShape2);
//        testPage.addShape(testShape4);
//        testPage2.addShape(testShape3);
//        currGame.addPage(testPage);
//        currGame.addPage(testPage2);
//        currGame.setCurPage(testPage);
//        //SAVE THE GAME loadGameintoJSON(Context, context, String fileName)
//        try {
//            currGame.loadGameintoJSON(this,"ABensonTestGame");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        try {
//            currGameSingleton.loadGame(this, "ABensonTestGame");
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
        currGame = currGameSingleton.getGame();
//        currGameSingleton.setSelectedShape(currGame.getPageList().get(0).getShapeList().get(0));
//        System.out.println(currGameSingleton.getSelectedShape().getContext());
    }

    //final new
    private boolean scriptReferCheck() {
        Set<String> pageNameSet = new HashSet<>();
        Set<String> shapeNameSet = new HashSet<>();

        for (Page currPage : currGame.getPageList()) {
            pageNameSet.add(currPage.getName());
            for (Shape currShape: currPage.getShapeList()) {
                shapeNameSet.add(currShape.getName());
            }
        }

        for (Page currPage : currGame.getPageList()) {
            for (Shape currShape : currPage.getShapeList()) {
                for (Shape.Action currAction : currShape.getOnClickActions()) {
                    if (currAction.getAction().equals("goto")) {
                        if (!pageNameSet.contains(currAction.getObjectName())) {
                            Toast.makeText(this, "Page " + currAction.getObjectName()
                            + " does not exist, but is referred to in the onClick script of "
                                    + currShape.getName(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } else if (!currAction.getAction().equals("play")) {
                        if (!shapeNameSet.contains(currAction.getObjectName())) {
                            Toast.makeText(this, "Shape " + currAction.getObjectName()
                            + " does not exist, but is referred to in the onClick script of "
                                    + currShape.getName(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
                for (Shape.Action currAction : currShape.getOnEnterActions()) {
                    if (currAction.getAction().equals("goto")) {
                        if (!pageNameSet.contains(currAction.getObjectName())) {
                            Toast.makeText(this, "Page " + currAction.getObjectName()
                            + " does not exist, but is referred to in the onEnter script of "
                                    + currShape.getName(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } else if (!currAction.getAction().equals("play")) {
                        if (!shapeNameSet.contains(currAction.getObjectName())) {
                            Toast.makeText(this, "Shape " + currAction.getObjectName()
                            + " does not exist, but is referred to in the onEnter script of "
                                    + currShape.getName(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
                for (Map.Entry<String, List<Shape.Action>> dropShape :
                        currShape.getOnDropActions().entrySet()) {
                    if (!shapeNameSet.contains(dropShape.getKey())) {
                        Toast.makeText(this, "Shape " + dropShape.getKey()
                        + " does not exist, but is referred to in the onDrop script of "
                                + currShape.getName(), Toast.LENGTH_LONG).show();
                        return false;
                    }
                    for (Shape.Action currAction : dropShape.getValue()) {
                        if (currAction.getAction().equals("goto")) {
                            if (!pageNameSet.contains(currAction.getObjectName())) {
                                Toast.makeText(this, "Page "
                                + currAction.getObjectName()
                                + " does not exist, but is referred to in the onDrop script of "
                                        + currShape.getName(), Toast.LENGTH_LONG).show();
                                return false;
                            }
                        } else if (!currAction.getAction().equals("play")) {
                            if (!shapeNameSet.contains(currAction.getObjectName())) {
                                Toast.makeText(this, "Shape "
                                + currAction.getObjectName()
                                + " does not exist, but is referred to in the onDrop script of "
                                        + currShape.getName(), Toast.LENGTH_LONG).show();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editshapemenu, menu);
        return true;
    }

    public void selectShape(MenuItem item) {
        if (noSelected()) return;

        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectShapeView = layoutInflater.inflate(R.layout.popup_edit_ondropshapeselect, null);
        selectShape = new PopupWindow(selectShapeView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        selectShape.setElevation(20);
        selectShape.showAtLocation(editPage, Gravity.CENTER, 0, 0);

        String[] shapeNames = getAllShapeNames(currGame);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, shapeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerItems = (Spinner) selectShapeView.findViewById(R.id.shapes);
        spinnerItems.setAdapter(adapter);
    }

    private String[] getAllShapeNames(Game currGame) {
        List<String> shapeNamesList = new LinkedList<>();
        for (Page currPage : currGame.getPageList()) {
            for (Shape currShape : currPage.getShapeList()) {
                StringBuilder curr = new StringBuilder();
                curr.append(currShape.getName());
                curr.append(" (");
                if (currShape.getText().length() != 0) {
                    curr.append(currShape.getText());
                } else if (currShape.getImgName().length() != 0) {
                    curr.append(currShape.getImgName());
                } else {
                    curr.append("grey box");
                }
                curr.append(")");
                shapeNamesList.add(curr.toString());
            }
        }

        String[] shapeNames = new String[shapeNamesList.size() + 1];
        shapeNames[0] = "";
        int idx = 0;
        for (String s : shapeNamesList) {
            shapeNames[++idx] = s;
        }
        return shapeNames;
    }

    private boolean noSelected() {
        if (GameSingleton.getGameInstance().getSelectedShape() == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View warningCustomView = layoutInflater.inflate(
                    R.layout.edit_mode_popup_without_selected_shape,null);
            Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(
                    R.id.leaveWarningPopupBtn);
            PopupWindow warningPopupWindow = new PopupWindow(
                    warningCustomView,ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            warningPopupWindow.setElevation(20);
            warningPopupWindow.showAtLocation(editPage,Gravity.CENTER,0,0);
            leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    warningPopupWindow.dismiss();
                }
            });
            return true;
        }
        return false;
    }

    public void closeSelectShape(View view) {
        selectShape.dismiss();
    }

    public void createOnDrop(View view) {

        Spinner spinnerItems = (Spinner) selectShapeView.findViewById(R.id.shapes);
        String selectedShape = spinnerItems.getSelectedItem().toString();

        if (selectedShape.equals("")) return;

        // final new
        selectedShape = getCleanShapeName(selectedShape);
        if (selectedShape.equalsIgnoreCase(currGameSingleton.getSelectedShape().getName())) {
            Toast.makeText(this, "on drop itself is not reasonable",
                    Toast.LENGTH_LONG).show();
            return;
        }

        currScript.append("on drop ");
        currScript.append(selectedShape);
        // final new

        selectShape.dismiss();
        setScript();
    }

    private String getCleanShapeName(String dirtyShapeName) {
        int lastBracket = -1;
        for (int i = 0; i < dirtyShapeName.length(); ++i) {
            if (dirtyShapeName.charAt(i) == '(') lastBracket = i;
        }
        return dirtyShapeName.substring(0, lastBracket - 1);
    }


    public void createOnClick(MenuItem item) {
        if (noSelected()) return;

        currScript.append("on click");
        setScript();
    }

    public void createOnEnter(MenuItem item) {
        if (noSelected()) return;

        currScript.append("on enter");
        setScript();
    }


    private void setScript() {
        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setScriptView = layoutInflater.inflate(R.layout.popup_edit_setscript, null);
        setScript = new PopupWindow(setScriptView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setScript.setElevation(20);
        setScript.showAtLocation(editPage, Gravity.CENTER, 0, 0);

        TextView currentScript = (TextView) setScriptView.findViewById(R.id.script);
        currentScript.setText(currScript);

        String[] shapeNames = getAllShapeNames(currGame);
        ArrayAdapter<String> shapesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, shapeNames);
        shapesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner show = (Spinner) setScriptView.findViewById(R.id.show);
        show.setAdapter(shapesAdapter);
        Spinner hide = (Spinner) setScriptView.findViewById(R.id.hide);
        hide.setAdapter(shapesAdapter);

        String[] pageNames = getAllPageNames(currGame);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, pageNames);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goTo = (Spinner) setScriptView.findViewById(R.id.goTo);
        goTo.setAdapter(pagesAdapter);

//        String[] soundNames = new String[]{"", "carrotcarrotcarrot", "evillaugh", "fire", "hooray",
//                                            "munch", "munching", "woof"};
//        ArrayAdapter<String> soundsAdapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_spinner_item, soundNames);
//        soundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner play = (Spinner) setScriptView.findViewById(R.id.play);
//        play.setAdapter(soundsAdapter);
    }

    private String[] getAllPageNames(Game currGame) {
        List<String> pageNamesList = new LinkedList<>();
        for (Page currPage : currGame.getPageList()) {
            pageNamesList.add(currPage.getName());
        }

        String[] pageNames = new String[pageNamesList.size() + 1];
        pageNames[0] = "";
        int idx = 0;
        for (String s : pageNamesList) {
            pageNames[++idx] = s;
        }
        return pageNames;
    }

    public void appendScript(View view) {
        Spinner goTo = (Spinner) setScriptView.findViewById(R.id.goTo);
        String goToSelected = goTo.getSelectedItem().toString();
        Spinner play = (Spinner) setScriptView.findViewById(R.id.play);
        String playSelected = play.getSelectedItem().toString();
        Spinner show = (Spinner) setScriptView.findViewById(R.id.show);
        String showSelected = show.getSelectedItem().toString();
        Spinner hide = (Spinner) setScriptView.findViewById(R.id.hide);
        String hideSelected = hide.getSelectedItem().toString();

        if (goToSelected.length() != 0) {
            currScript.append(" goto ");
            currScript.append(goToSelected);
        }
        if (playSelected.length() != 0) {
            currScript.append(" play ");
            currScript.append(playSelected);
        }
        if (showSelected.length() != 0) {
            currScript.append(" show ");
            currScript.append(getCleanShapeName(showSelected));
        }
        if (hideSelected.length() != 0) {
            currScript.append(" hide ");
            currScript.append(getCleanShapeName(hideSelected));
        }

        TextView currentScript = (TextView) setScriptView.findViewById(R.id.script);
        currentScript.setText(currScript);
        // final new
        goTo.setSelection(0);
        play.setSelection(0);
        show.setSelection(0);
        hide.setSelection(0);
    }

    public void completeScript(View view) {
        String script = currScript.toString();
        if (!script.contains("play") && !script.contains("show") && !script.contains("goto")
            && !script.contains("hide")) {
            return;
        }

        // final new
        int length = scriptSenseCheck(script);
        if (length != -1) {
            currScript.delete(length, currScript.length());
            TextView currentScript = (TextView) setScriptView.findViewById(R.id.script);
            currentScript.setText(currScript);
            return;
        }
        // final new
        Shape selectedShape = GameSingleton.getGameInstance().getSelectedShape();
        selectedShape.parseScript(script + ";");

        currScript = new StringBuilder();
        setScript.dismiss();
    }

    // final new
    private int scriptSenseCheck(String script) {
        String[] words = script.split(" ");
        for (int i = 0; i < words.length; ++i) {
            if (words[i].equals("goto") && i + 2 < words.length && words[i + 2].equals("goto")) {
                Toast.makeText(this, "2 consecutive 'goto' actions are not reasonable",
                        Toast.LENGTH_LONG).show();
                if (words[1].equals("drop")) {
                    return words[0].length() + words[1].length() + words[2].length() + 2;
                }
                return words[0].length() + words[1].length() + 1;
            }
            if ((words[i].equals("hide") || words[i].equals("show")) && i + 3 < words.length
                && (words[i + 2].equals("hide") || words[i + 2].equals("show"))
                    && words[i + 1].equalsIgnoreCase(words[i + 3])) {
                Toast.makeText(this, "2 consecutive 'show'/'hide' actions " +
                                "on the same object are not reasonable", Toast.LENGTH_LONG).show();
                if (words[1].equals("drop")) {
                    return words[0].length() + words[1].length() + words[2].length() + 2;
                }
                return words[0].length() + words[1].length() + 1;
            }
        }
        return -1;
    }

    public void closeSetScript(View view) {
        setScript.dismiss();
        currScript = new StringBuilder();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //disallow user to input space
        InputFilter spaceFilter = (source, start, end, dest, dstart, dend) -> {
            if (source.equals(" ")) {return "";}
            else { return null; }
        };
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Handle item selection
        if (item.getItemId() == R.id.createShape && editview.intialDuplicate()) {
            Toast.makeText(this, "Please move the overlapped shape at the initial location.", Toast.LENGTH_LONG).show();
            return true;
        } else {
            switch (item.getItemId()) {

                //TODO:ZIXUAN
                case R.id.bg1:
                    System.out.println("bg1");
                    editview.createBG("bg1");
                    return true;
                case R.id.bg2:
                    editview.createBG("bg2");
                    return true;
                case R.id.bg3:
                    editview.createBG("bg3");
                    return true;
                case R.id.bg4:
                    editview.createBG("bg4");
                    return true;
                case R.id.clearbg:
                    editview.clearBG();
                    return true;

                case R.id.carrot:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("carrot",false);
                    }
                    return true;
                case R.id.carrot2:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("carrot2", false);
                    }
                    return true;
                case R.id.death:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("death",false);
                    }
                    return true;
                case R.id.duck:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("duck",false);
                    }
                    return true;
                case R.id.fire:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("fire",false);
                    }
                    return true;
                case R.id.mystic:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("mystic",false);
                    }
                    return true;
                case R.id.defaultShape:
                    if (!editview.intialDuplicate()) {
                        editview.createShape("",false);
                    }
                    return true;
                case R.id.pasteShape:
                    Shape paste = GameSingleton.getGameInstance().getCopiedShape();
                    if (paste == null) {
                        Toast.makeText(this, "Please copy a shape first.", Toast.LENGTH_LONG).show();
                    } else if (editview.copyDuplicate(paste)){
                        Toast.makeText(this, "Please move the overlapped shape at the initial location.", Toast.LENGTH_LONG).show();
                    } else {
                        editview.pasteShape(paste);
                    }
                    return true;
                case R.id.textShape:
                    if (editview.intialDuplicate()) return true;
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popup = inflater.inflate(R.layout.popup_textshape, null);

                    //Create a popup window:
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    PopupWindow popupEditText = new PopupWindow(popup, width, height, focusable);
                    popupEditText.setElevation(20);
                    popupEditText.showAtLocation(editview, Gravity.CENTER, 0, 0);

                    //Click Button:
                    Button enterText = (Button) popup.findViewById(R.id.textEnter);
                    enterText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Enter Text:
                            EditText edittext = (EditText) popup.findViewById(R.id.textInput);
                            String textInput = edittext.getText().toString();

                            String text = textInput.trim();
                            boolean allSpace = true;
                            for(int i = 0; i < text.length(); i++){
                                if(text.charAt(i) != ' ') {
                                    allSpace = false;
                                    break;
                                }
                            }
                            if(allSpace) {
                                text = "";
                            }

                            editview.createShape(text, true);
                            popupEditText.dismiss();
                        }
                    });

                    Button cancel = (Button) popup.findViewById(R.id.textCancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupEditText.dismiss();
                        }
                    });
                    return true;
                case R.id.setPropertyItem:
                    if (GameSingleton.getGameInstance().getSelectedShape() == null) {
                        View warningCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_without_selected_shape, null);
                        Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(R.id.leaveWarningPopupBtn);
                        PopupWindow warningPopupWindow = new PopupWindow(warningCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        warningPopupWindow.setElevation(20);
                        warningPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                        leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningPopupWindow.dismiss();
                            }
                        });
                        return true;
                    }
                    View setPropertyCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_set_property, null);

                    Button setPropertyPopupBtn = (Button) setPropertyCustomView.findViewById(R.id.setPropertyPopupBtn);
                    Button closeSetPropertyPagePopupBtn = (Button) setPropertyCustomView.findViewById(R.id.closeSetProperShapePopupBtn);

                    PopupWindow setPropertyPopupWindow = new PopupWindow(setPropertyCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    setPropertyPopupWindow.setElevation(20);
                    setPropertyPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                    setPropertyPopupWindow.setFocusable(true);
                    setPropertyPopupWindow.update();

                    TextView imageNameTextView = setPropertyCustomView.findViewById(R.id.imageNamePopup);
                    imageNameTextView.setText(GameSingleton.getGameInstance().getSelectedShape().getImgName());

                    TextView shapeTypeTextView = setPropertyCustomView.findViewById(R.id.shapeTypePopupInput);
                    if(!GameSingleton.getGameInstance().getSelectedShape().getText().equals("")){
                        shapeTypeTextView.setText("Text");
                    }else if(!GameSingleton.getGameInstance().getSelectedShape().getImgName().equals("")){
                        shapeTypeTextView.setText("Bitmap");
                    }else{
                        shapeTypeTextView.setText("Grey Rect");
                    }


                    EditText shapeNameEditText = setPropertyCustomView.findViewById(R.id.shapeNamePopupInput);
                    shapeNameEditText.setText(GameSingleton.getGameInstance().getSelectedShape().getName());
                    shapeNameEditText.setFilters(new InputFilter[]{spaceFilter});



                    EditText shapeXEditText = setPropertyCustomView.findViewById(R.id.shapeXPopupInput);
                    shapeXEditText.setText(String.valueOf(GameSingleton.getGameInstance().getSelectedShape().getX()));

                    EditText shapeYEditText = setPropertyCustomView.findViewById(R.id.shapeYPopupInput);
                    shapeYEditText.setText(String.valueOf(GameSingleton.getGameInstance().getSelectedShape().getY()));

                    EditText shapeWidthEditText = setPropertyCustomView.findViewById(R.id.shapeWidthPopupInput);
                    shapeWidthEditText.setText(String.valueOf(GameSingleton.getGameInstance().getSelectedShape().getWidth()));
                    if(GameSingleton.getGameInstance().getSelectedShape().getText().length() != 0){
                        shapeWidthEditText.setEnabled(false);
                    }

                    EditText shapeHeightEditText = setPropertyCustomView.findViewById(R.id.shapeHeightPopupInput);
                    shapeHeightEditText.setText(String.valueOf(GameSingleton.getGameInstance().getSelectedShape().getHeight()));
                    if(GameSingleton.getGameInstance().getSelectedShape().getText().length() != 0){
                        shapeHeightEditText.setEnabled(false);
                    }

                    CheckBox movableCheckBox = setPropertyCustomView.findViewById(R.id.movablePopupCheckbox);
                    if(GameSingleton.getGameInstance().getSelectedShape().isMovable()){
                        movableCheckBox.setChecked(true);
                    }else{
                        movableCheckBox.setChecked(false);
                    }

                    CheckBox hiddenCheckBox = setPropertyCustomView.findViewById(R.id.hiddenPopupCheckbox);
                    if(GameSingleton.getGameInstance().getSelectedShape().isVisible()){
                        hiddenCheckBox.setChecked(false);
                    }else{
                        hiddenCheckBox.setChecked(true);
                    }

                    EditText scalingEditText = setPropertyCustomView.findViewById(R.id.scalingPopupInput);
                    if(GameSingleton.getGameInstance().getSelectedShape().getText().length() != 0){
                        scalingEditText.setEnabled(false);
                    }

                    setPropertyPopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Todo: check repeated name with chao's api
                            float x =Float.parseFloat(shapeXEditText.getText().toString());
                            float y =Float.parseFloat(shapeYEditText.getText().toString());
                            float originalWidth = Float.parseFloat(shapeWidthEditText.getText().toString());
                            float originalHeight = Float.parseFloat(shapeHeightEditText.getText().toString());
                            float scalingRatio = Float.parseFloat(scalingEditText.getText().toString()) / 100;
                            float width = originalWidth * scalingRatio;
                            float height = originalHeight * scalingRatio;

                            if(editview.nameRepeated(GameSingleton.getGameInstance().getSelectedShape(), shapeNameEditText.getText().toString())){
                                Toast toast = Toast.makeText(EditMode.this, "Repeated shape name, please change",Toast.LENGTH_LONG);
                                toast.show();
                                setPropertyPopupWindow.dismiss();
                                return;
                            }
                            GameSingleton.getGameInstance().getSelectedShape().setName(shapeNameEditText.getText().toString());

                            if(x> viewWidth || y> viewHeight|| x + width > viewWidth || y + height > viewHeight){
                                Toast toast = Toast.makeText(EditMode.this, "Out of boundary, please set again",Toast.LENGTH_LONG);
                                toast.show();
                                setPropertyPopupWindow.dismiss();
                                return;
                            }

                            if(editview.isOverLap(shapeNameEditText.getText().toString(),x,y,width,height)){
                                Toast toast = Toast.makeText(EditMode.this, "Overlapped, please set again",Toast.LENGTH_LONG);
                                toast.show();
                                setPropertyPopupWindow.dismiss();
                                return;
                            }

                            GameSingleton.getGameInstance().getSelectedShape().setX(Float.parseFloat(shapeXEditText.getText().toString()));
                            GameSingleton.getGameInstance().getSelectedShape().setY(Float.parseFloat(shapeYEditText.getText().toString()));
                            GameSingleton.getGameInstance().getSelectedShape().setWidth(width);
                            GameSingleton.getGameInstance().getSelectedShape().setHeight(height);
                            if(movableCheckBox.isChecked()){
                                GameSingleton.getGameInstance().getSelectedShape().setMovable(true);
                            }else{
                                GameSingleton.getGameInstance().getSelectedShape().setMovable(false);
                            }
                            if(hiddenCheckBox.isChecked()){
                                GameSingleton.getGameInstance().getSelectedShape().setVisible(false);
                            }else{
                                GameSingleton.getGameInstance().getSelectedShape().setVisible(true);
                            }
                            editview.invalidate();
                            setPropertyPopupWindow.dismiss();
                        }
                    });
                    closeSetPropertyPagePopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPropertyPopupWindow.dismiss();
                        }
                    });
                    return true;
//
                case R.id.changeImageItem:
                    if (GameSingleton.getGameInstance().getSelectedShape() == null) {
                        View warningCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_without_selected_shape, null);
                        Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(R.id.leaveWarningPopupBtn);
                        PopupWindow warningPopupWindow = new PopupWindow(warningCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        warningPopupWindow.setElevation(20);
                        warningPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                        leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningPopupWindow.dismiss();
                            }
                        });
                        return true;
                    }
                    View changeImageCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_change_image, null);

                    Button changeImagePopupBtn = (Button) changeImageCustomView.findViewById(R.id.changeImagePopupBtn);
                    Button closeChangeImagePagePopupBtn = (Button) changeImageCustomView.findViewById(R.id.closeChangeImagePopupBtn);

                    Spinner changeImageSpinner = (Spinner) changeImageCustomView.findViewById(R.id.changeImageSpinner);

                    PopupWindow changeImagePopupWindow = new PopupWindow(changeImageCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    changeImagePopupWindow.setElevation(20);
                    changeImagePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);

                    changeImagePopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String imageName = changeImageSpinner.getSelectedItem().toString();
                            if(imageName.equals("no image")){
                                imageName = "";
                            }
                            GameSingleton.getGameInstance().getSelectedShape().setImgName(imageName);
                            editview.invalidate();
                            changeImagePopupWindow.dismiss();
                        }
                    });
                    closeChangeImagePagePopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeImagePopupWindow.dismiss();
                        }
                    });
                    return true;


                case R.id.showScriptItem:
                    if (GameSingleton.getGameInstance().getSelectedShape() == null) {
                        View warningCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_without_selected_shape, null);
                        Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(R.id.leaveWarningPopupBtn);
                        PopupWindow warningPopupWindow = new PopupWindow(warningCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        warningPopupWindow.setElevation(20);
                        warningPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                        leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningPopupWindow.dismiss();
                            }
                        });
                        return true;
                    }
                    View showScriptCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_show_script, null);
                    Button deleteScriptPopupBtn = (Button) showScriptCustomView.findViewById(R.id.deleteScriptPopupBtn);
                    Button closeShowScriptShapePopupBtn = (Button) showScriptCustomView.findViewById(R.id.closeShowScriptPopupBtn);

                    PopupWindow showScriptPopupWindow = new PopupWindow(showScriptCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    showScriptPopupWindow.setElevation(20);
                    showScriptPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);

                    List<String> allScriptsList = GameSingleton.getGameInstance().getSelectedShape().getAllScripts();
                    String onClickScript = "",onEnterScript = "";
                    List<String> onDropScriptList = new ArrayList<>();
                    String[] onDropScriptArray = new String[]{};
                    if(allScriptsList.size() != 0) {
                        for (String elem : allScriptsList) {
                            if (elem.startsWith("on click")) {
                                onClickScript = elem;
                                break;
                            }
                        }
                        for (String elem : allScriptsList) {
                            if (elem.startsWith("on enter")) {
                                onEnterScript = elem;
                                break;
                            }
                        }
                        for (String elem : allScriptsList){
                            if (elem.startsWith("on drop")){
                                onDropScriptList.add(elem);
                            }
                        }
                    }

                    onDropScriptArray = onDropScriptList.toArray(new String[onDropScriptList.size()]);

                    TextView onClickTextView = showScriptCustomView.findViewById(R.id.onClickScript);
                    onClickTextView.setText(onClickScript);

                    TextView onEnterTextView = showScriptCustomView.findViewById(R.id.onEnterScript);
                    onEnterTextView.setText(onEnterScript);



                    Spinner onDropSpinner = (Spinner) showScriptCustomView.findViewById(R.id.onDropSpinner);
                    SpinnerAdapter onDropSpinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,onDropScriptArray);
                    onDropSpinner.setAdapter(onDropSpinnerAdapter);

                    CheckBox onClickCheckBox = showScriptCustomView.findViewById(R.id.onClickCheckBox);
                    CheckBox onEnterCheckBox = showScriptCustomView.findViewById(R.id.onEnterCheckBox);
                    CheckBox onDropCheckBox = showScriptCustomView.findViewById(R.id.onDropCheckBox);


                    deleteScriptPopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onClickCheckBox.isChecked() && !onClickTextView.getText().toString().equals("")){
                                GameSingleton.getGameInstance().getSelectedShape().clearScript(onClickTextView.getText().toString());
                            }

                            if(onEnterCheckBox.isChecked() && !onEnterTextView.getText().toString().equals("")){
                                GameSingleton.getGameInstance().getSelectedShape().clearScript(onEnterTextView.getText().toString());
                            }

                            if(onDropCheckBox.isChecked() && !onDropSpinner.getSelectedItem().toString().equals("")){
                                GameSingleton.getGameInstance().getSelectedShape().clearScript(onDropSpinner.getSelectedItem().toString());
                            }
                            showScriptPopupWindow.dismiss();
                        }
                    });

                    closeShowScriptShapePopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showScriptPopupWindow.dismiss();
                        }
                    });
                    return true;

                case R.id.setTextItem:
                    if (GameSingleton.getGameInstance().getSelectedShape() == null) {
                        View warningCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_without_selected_shape, null);
                        Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(R.id.leaveWarningPopupBtn);
                        PopupWindow warningPopupWindow = new PopupWindow(warningCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        warningPopupWindow.setElevation(20);
                        warningPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                        leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningPopupWindow.dismiss();
                            }
                        });
                        return true;
                    }
                    View setTextCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_set_text, null);
                    Button setTextPopupBtn = (Button) setTextCustomView.findViewById(R.id.setTextPopupBtn);
                    Button closeSetTextPopupBtn = (Button) setTextCustomView.findViewById(R.id.closeSetTextShapePopupBtn);

                    PopupWindow setTextPopupWindow = new PopupWindow(setTextCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    setTextPopupWindow.setElevation(20);
                    setTextPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                    setTextPopupWindow.setFocusable(true);
                    setTextPopupWindow.update();

                    EditText shapeTextEditText = setTextCustomView.findViewById(R.id.shapeTextPopupInput);
                    shapeTextEditText.setText(GameSingleton.getGameInstance().getSelectedShape().getText());

                    EditText fontSizeEditText = setTextCustomView.findViewById(R.id.shapeFontSizePopupInput);
                    fontSizeEditText.setText(String.valueOf(GameSingleton.getGameInstance().getSelectedShape().getFontSize()));

                    CheckBox boldCheckBox = setTextCustomView.findViewById(R.id.boldPopupCheckbox);
                    boldCheckBox.setChecked(GameSingleton.getGameInstance().getSelectedShape().isBold());

                    CheckBox italicCheckBox = setTextCustomView.findViewById(R.id.italicPopupCheckbox);
                    italicCheckBox.setChecked(GameSingleton.getGameInstance().getSelectedShape().isItalic());

                    CheckBox underlineCheckBox = setTextCustomView.findViewById(R.id.underlinePopupCheckbox);
                    underlineCheckBox.setChecked(GameSingleton.getGameInstance().getSelectedShape().isUnderline());



                    setTextPopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = shapeTextEditText.getText().toString().trim();
                            float fontSize = Float.parseFloat(fontSizeEditText.getText().toString());
                            boolean isBold = boldCheckBox.isChecked();
                            boolean isItalic = italicCheckBox.isChecked();
                            boolean isUnderline = underlineCheckBox.isChecked();

                            boolean allSpace = true;
                            for(int i = 0; i < text.length(); i++){
                                if(text.charAt(i) != ' ') {
                                    allSpace = false;
                                    break;
                                }
                            }
                            if(allSpace) {
                                text = "";
                            }

                            if(text.length() == 0){
                                GameSingleton.getGameInstance().getSelectedShape().setText("");
                                editview.invalidate();
                                setTextPopupWindow.dismiss();
                                return;
                            }
                            float[] futureTextProperty = GameSingleton.getGameInstance().getSelectedShape().getFutureTextProperty(text,fontSize,isBold,isItalic,isUnderline);

                            if(futureTextProperty[0]> viewWidth || futureTextProperty[1]> viewHeight
                                    || futureTextProperty[0]+ futureTextProperty[2]> viewWidth || futureTextProperty[1] + futureTextProperty[3] > viewHeight){
                                setTextPopupWindow.dismiss();
                                Toast toast = Toast.makeText(EditMode.this, "Out of Boundary, please set again!",Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }

                            if(editview.isOverLap(GameSingleton.getGameInstance().getSelectedShape().getName(),futureTextProperty[0],
                                    futureTextProperty[1],futureTextProperty[2],futureTextProperty[3])){
                                Toast toast = Toast.makeText(EditMode.this, "Overlapped, please set again",Toast.LENGTH_LONG);
                                toast.show();
                                setTextPopupWindow.dismiss();
                                return;
                            }


                            GameSingleton.getGameInstance().getSelectedShape().setFontSize(fontSize);
                            GameSingleton.getGameInstance().getSelectedShape().setBold(isBold);
                            GameSingleton.getGameInstance().getSelectedShape().setItalic(isItalic);
                            GameSingleton.getGameInstance().getSelectedShape().setUnderline(isUnderline);
                            GameSingleton.getGameInstance().getSelectedShape().setText(text);
                            GameSingleton.getGameInstance().getSelectedShape().applyTextSet();


                            editview.invalidate();
                            setTextPopupWindow.dismiss();
                        }
                    });
                    closeSetTextPopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setTextPopupWindow.dismiss();
                        }
                    });
                    return true;

                case R.id.deleteShapeItem:
                    if (GameSingleton.getGameInstance().getSelectedShape() == null) {
                        View warningCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_without_selected_shape, null);
                        Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(R.id.leaveWarningPopupBtn);
                        PopupWindow warningPopupWindow = new PopupWindow(warningCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        warningPopupWindow.setElevation(20);
                        warningPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                        leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningPopupWindow.dismiss();
                            }
                        });
                        return true;
                    }
                    View deleteShapeCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_delete_shape, null);
                    Button deleteShapePopupBtn = (Button) deleteShapeCustomView.findViewById(R.id.deleteShapePopupBtn);
                    Button closeDeleteShapePopupBtn = (Button) deleteShapeCustomView.findViewById(R.id.closeDeleteShapePopupBtn);

                    PopupWindow deleteShapePopupWindow = new PopupWindow(deleteShapeCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    deleteShapePopupWindow.setElevation(20);
                    deleteShapePopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);

                    deleteShapePopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            currGame.getCurPage().removeShape(GameSingleton.getGameInstance().getSelectedShape());
                            GameSingleton.getGameInstance().setSelectedShape(null);
                            editview.invalidate();
                            deleteShapePopupWindow.dismiss();
                        }
                    });
                    closeDeleteShapePopupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteShapePopupWindow.dismiss();
                        }
                    });
                    return true;

                case R.id.copyShapeItem:
                    if (GameSingleton.getGameInstance().getSelectedShape() == null) {
                        View warningCustomView = layoutInflater.inflate(R.layout.edit_mode_popup_without_selected_shape, null);
                        Button leaveWarningPopupBtn = (Button) warningCustomView.findViewById(R.id.leaveWarningPopupBtn);
                        PopupWindow warningPopupWindow = new PopupWindow(warningCustomView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        warningPopupWindow.setElevation(20);
                        warningPopupWindow.showAtLocation(editPage, Gravity.CENTER, 0, 0);
                        leaveWarningPopupBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warningPopupWindow.dismiss();
                            }
                        });
                        return true;
                    }



                    Shape copiedShape = GameSingleton.getGameInstance().getSelectedShape().copyShape();
                    GameSingleton.getGameInstance().setCopiedShape(copiedShape);
                    Toast toast = Toast.makeText(EditMode.this, "Copied successful, you can paste it in create shape now",Toast.LENGTH_LONG);
                    toast.show();

                    return true;

                default:
                    return super.onOptionsItemSelected(item);

            }
        }
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    private String update(String s){
        if(s.length() > 10){
            s = s.substring(0, 11)+"...";
        }
        return s;
    }

    // final new
    public void setBGM(MenuItem item) {
        String bgm = getResources().getResourceName(item.getItemId()).substring(
                "edu.stanford.cs108.bunnyworld:id/".length());
        if (bgm.equals("empty")) {
            currGame.getCurPage().setBGM("");
            return;
        }
        currGame.getCurPage().setBGM(bgm);
    }

    private boolean isRepeated(String name){
        for(Page p: currGame.getPageList()){
            if(p.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }


}