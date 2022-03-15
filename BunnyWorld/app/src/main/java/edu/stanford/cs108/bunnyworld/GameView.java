package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameView extends View {
    private boolean changePage, isClick;
    private Page previousPage, currentPage;
    private float x1, y1, x2, y2;
    private float top, left, bottom, right;
    private Paint possessionsLine;
    private Game currentGame;
    private String pageName;
    private static float inventoryBound;
    private int viewWidth, viewHeight;
    private MediaPlayer mp; //final new

    public GameView(Context context, AttributeSet attrs) throws IOException, JSONException {
        super(context, attrs);
        init();
    }

    private static String gameName;
    public static void setGameName(String input){
        gameName = input;
    }
    private void init() throws IOException, JSONException {
        //LOAD THE GAME BY USING loadJSONIntoGame(Context context, String fileName)
        currentGame = new Game();
        currentGame.loadJSONIntoGame(this.getContext(), gameName);
//
//        Page testPage = new Page("test", currentGame);
//        Page testPage2 = new Page("test2", currentGame);
//        Shape testShape = new Shape("text", 250, 100, 100, 100,
//                getContext(), testPage);
//        Shape testShape2 = new Shape("carrot", 100, 100, 100, 100,
//                getContext(), testPage);
//        Shape testShape3 = new Shape("greyBox", 100, 100, 100, 100,
//                getContext(), testPage2);
//        Shape testShape4 = new Shape("rect", 100, 100, 100, 100,
//                getContext(), testPage);
//        testShape2.setImgName("carrot");
//        testShape.parseScript("on click play evillaugh;");
//        testShape.parseScript("on click goto test2;");
//        testShape2.parseScript("on drop text play evillaugh;");
//        testShape2.parseScript("on drop rect play evillaugh;");
//        testShape.setText("no leetcode then dog0");
//        testShape3.parseScript("on click goto test;");
//        testShape3.parseScript("on enter play evillaugh;");
//        testShape.setFontSize(100f);
//        testPage.addShape(testShape);
//        testPage.addShape(testShape2);
//        testPage.addShape(testShape4);
//        testPage2.addShape(testShape3);
//        currentGame.addPage(testPage);
//        currentGame.addPage(testPage2);
//        currentGame.setCurPage(testPage);
//        //SAVE THE GAME loadGameintoJSON(Context, context, String fileName)
//        currentGame.loadGameintoJSON(this.getContext(),"testGame0");
//        testShape.setText("no leetcode then dog1");
//        currentGame.loadGameintoJSON(this.getContext(),"testGame1");
//        testShape.setText("no leetcode then dog2");
//        currentGame.loadGameintoJSON(this.getContext(),"testGame2");

        currentGame.setCurPage(currentGame.getPageList().get(0));
        currentPage = currentGame.getCurPage();
        possessionsLine = new Paint();
        possessionsLine.setColor(Color.BLACK);
        possessionsLine.setStyle(Paint.Style.STROKE);
        possessionsLine.setStrokeWidth(5.0f);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;
        inventoryBound = 0.75f * viewHeight;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        currentPage = currentGame.getCurPage();

        if (previousPage == null || !previousPage.getName().equalsIgnoreCase(currentPage.getName())) {
            if (previousPage != null) {
                changePage = true;
                clickedShape = null;
            }
            currentPage.enter();
            if (!currentGame.getCurPage().getName().equalsIgnoreCase(currentPage.getName())) {
                invalidate();
            }
            //final new
            playBGM(currentPage.getBGM());
            currentPage.setBoundaryHeight(inventoryBound);
            previousPage = currentPage;
        }

        currentPage.draw(canvas);
        if (clickedShape != null) {
            for (Shape shape: currentPage.getShapeList()) {
                //System.out.println(shape);
                if (shape.isDroppable(clickedShape.getName())) {
                    shape.drawGreenOutline();
                }
            }
        }

        canvas.drawLine(0.0f, inventoryBound,
                viewWidth, inventoryBound, possessionsLine);
    }

    //final new
    private void playBGM(String bgm) {
        if (mp != null) mp.stop();
        if (bgm.equals("")) return;
        int soundId = getContext().getResources().getIdentifier(
                bgm, "raw", getContext().getPackageName());
        mp = MediaPlayer.create(getContext(), soundId);
        mp.start();
    }
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility != View.VISIBLE) {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
            super.onWindowVisibilityChanged(visibility);
        } else {
            super.onWindowVisibilityChanged(visibility);
            playBGM(currentPage.getBGM());
        }
    }


    private Shape underLayerShape, clickedShape;
    private boolean insideShape = false;
    private boolean inInventory = false;
    private float xDistance, yDistance;
    private long startTime = 0;
    private long endTime = 0;
    private float inventoryEdge = 5;

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                System.out.println("clicked + 1");
//                x1 = event.getX();
//                y1 = event.getY();
//                changePage = false;
//
//                clickedShape = currentPage.selectShape(x1, y1);
//                startTime = System.currentTimeMillis();
//                if (clickedShape != null) {
//                    insideShape = true;
//                    xDistance = clickedShape.getX() - x1;
//                    yDistance = clickedShape.getY() - y1;
//                }
//
//
//                invalidate();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                x2 = event.getX();
//                y2 = event.getY();
//                //isClick = false;
//                if (insideShape && !changePage) {
//                    currentPage.updateCurShape(x2 + xDistance, y2 + yDistance);
//                    invalidate();
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                y2 = event.getY();
//                endTime = System.currentTimeMillis();
//                //System.out.println(endTime - startTime + " ms");
//                if ((endTime - startTime) > 500) {
//                    isClick = false;
//                } else {
//                    changePage = false;
//                    isClick = true;
//                }
//
//                if (x1 > x2) {
//                    left = x2;
//                    right = x1;
//                } else {
//                    left = x1;
//                    right = x2;
//                }
//
//                if (y1 > y2) {
//                    top = y2;
//                    bottom = y1;
//                } else {
//                    top = y1;
//                    bottom = y2;
//                }
//
//                if (clickedShape != null && insideShape && !changePage) {
//                    boolean overlapped = false;
//                    //Check inventory:
//                    float newX = x2 + xDistance;
//                    float newY = y2 + yDistance;
//                    if (isClick && newY + clickedShape.getHeight()/2 < inventoryBound) {
//                        clickedShape.clicked();
//                    }
//                    //inventory boundary
//                    if (newY + clickedShape.getHeight() > inventoryBound && newY < inventoryBound) {
//                        if (newY + clickedShape.getHeight() / 2 < inventoryBound) {
//                            newY = inventoryBound - clickedShape.getHeight();
//                        } else {
//                            newY = inventoryBound;
//                        }
//                    }
//
//
//                    float curShapeRight = newX + clickedShape.getWidth();
//                    float curShapeBottom = newY + clickedShape.getHeight();
//                    List<Shape> currShapeList = new ArrayList<>(currentPage.getShapeList());
//                    for (Shape shape : currShapeList) {
//                        if (!shape.getName().equalsIgnoreCase(clickedShape.getName()) &&
//                                !(newX > shape.getX() + shape.getWidth() ||
//                                        curShapeRight < shape.getX() ||
//                                        newY > shape.getY() + shape.getHeight() ||
//                                        curShapeBottom < shape.getY())) {
//                            System.out.println(shape.getName());
//                            underLayerShape = shape;
//                            inInventory = false;
//                            currentPage.overLap(underLayerShape, clickedShape, inInventory, x1 + xDistance, y1 + yDistance);
//                            overlapped = true;
//                        }
//                    }
//
//                    List<Shape> currInventoryList = new ArrayList<>(currentPage.getInventory());
//                    for (Shape shape : currInventoryList) {
//                        if (!shape.getName().equalsIgnoreCase(clickedShape.getName()) &&
//                                !(newX > shape.getX() + shape.getWidth() ||
//                                        curShapeRight < shape.getX() ||
//                                        newY > shape.getY() + shape.getHeight() ||
//                                        curShapeBottom < shape.getY())) {
//                            System.out.println(shape.getName());
//                            underLayerShape = shape;
//                            boolean inInventory = true;
//                            currentPage.overLap(underLayerShape, clickedShape, inInventory,x1 + xDistance, y1 + yDistance);
//                            overlapped = true;
//                        }
//                    }
//
//                    inInventory = false;
//
//                    clickedShape = null;
//                    insideShape = false;
//                    if (!overlapped) {
//                        currentPage.updateCurShape(newX, newY);
//                    }
//                    invalidate();
//                }
//        }
//        return true;
//    }
//}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
    //                System.out.println("clicked + 1");
                x1 = event.getX();
                y1 = event.getY();
                changePage = false;


                clickedShape = currentPage.selectShape(x1, y1);
                startTime = System.currentTimeMillis();
                if (clickedShape != null && clickedShape.isVisible()) {
                    insideShape = true;
                    xDistance = clickedShape.getX() - x1;
                    yDistance = clickedShape.getY() - y1;
                    invalidate();
                }



                break;

            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                //isClick = false;
                if (insideShape && !changePage && clickedShape.isVisible() &&
                clickedShape.isMovable()) {
                    currentPage.update(x2 + xDistance, y2 + yDistance);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                endTime = System.currentTimeMillis();
                //System.out.println(endTime - startTime + " ms");
                if ((endTime - startTime) > 200) {
                    isClick = false;
                } else {
                    changePage = false;
                    isClick = true;
                }

                if (x1 > x2) {
                    left = x2;
                    right = x1;
                } else {
                    left = x1;
                    right = x2;
                }

                if (y1 > y2) {
                    top = y2;
                    bottom = y1;
                } else {
                    top = y1;
                    bottom = y2;
                }

                if (clickedShape != null && insideShape && !changePage) {
                    if (isClick) {
                        if (!clickedShape.isInPossession()) {
                            clickedShape.clicked();
                        }
                    } else {
                        boolean overlapped = false;
                        //Check inventory:
                        float newX = x2 + xDistance;
                        float newY = y2 + yDistance;

                        //Check out of boundary:
                        if (!clickedShape.isInPossession() && (newX < 0 || newY < 0 ||
                                newX + clickedShape.getWidth() > viewWidth ||
                                newY + clickedShape.getInventoryShapeSize() > viewHeight)) {
                            currentPage.updateCurShape(x1 + xDistance, y1 + yDistance);
                        } else if (clickedShape.isInPossession() && (newX < 0 || newY < 0 ||
                                newX + clickedShape.getInventoryShapeSize() > viewWidth ||
                                newY + clickedShape.getInventoryShapeSize() > viewHeight)) {
                            currentPage.update(x1 + xDistance, y1 + yDistance);
                        } else {
                            //Check overlap:
                            float curShapeRight = newX + clickedShape.getWidth();
                            float curShapeBottom = newY + clickedShape.getHeight();
                            List<Shape> currShapeList = new ArrayList<>(currentPage.getShapeList());
                            for (Shape shape : currShapeList) {
                                if (!shape.getName().equals(clickedShape.getName()) &&
                                        !(newX > shape.getX() + shape.getWidth() ||
                                                curShapeRight < shape.getX() ||
                                                newY > shape.getY() + shape.getHeight() ||
                                                curShapeBottom < shape.getY())) {
                                    List<Shape> a = currentPage.getShapeList();
                                    underLayerShape = shape;
                                    boolean inPossessions = currentPage.isInInventory(newX,
                                            newY + clickedShape.getHeight() / 2);
                                    if (inPossessions && !clickedShape.isInPossession()) {
                                        currentPage.getShapeList().remove(clickedShape);
                                        currentPage.getInventory().add(clickedShape);
                                    } else if (!inPossessions && clickedShape.isInPossession()) {
                                        currentPage.getInventory().remove(clickedShape);
                                        currentPage.getShapeList().add(clickedShape);
                                    }
                                    currentPage.overLap(underLayerShape, clickedShape, false, x1 + xDistance, y1 + yDistance);
                                    overlapped = true;
                                }
                            }

                            List<Shape> currInventoryList = new ArrayList<>(currentPage.getInventory());
                            for (Shape shape : currInventoryList) {
                                if (!shape.getName().equals(clickedShape.getName()) &&
                                        !(newX > shape.getX() + shape.getInventoryShapeSize() ||
                                                curShapeRight < shape.getX() ||
                                                newY > shape.getY() + shape.getInventoryShapeSize() ||
                                                curShapeBottom < shape.getY())) {
                                    underLayerShape = shape;
                                    boolean inPossessions = currentPage.isInInventory(newX,
                                            newY + clickedShape.getHeight() / 2);
                                    if (inPossessions && !clickedShape.isInPossession()) {
                                        currentPage.getShapeList().remove(clickedShape);
                                        currentPage.getInventory().add(clickedShape);
                                    } else if (!inPossessions && clickedShape.isInPossession()) {
                                        currentPage.getInventory().remove(clickedShape);
                                        currentPage.getShapeList().add(clickedShape);
                                    }
                                    currentPage.overLap(underLayerShape, clickedShape, true, x1 + xDistance, y1 + yDistance);
                                    overlapped = true;
                                }
                            }

                            if (!overlapped) {
                                //inventory boundary
                                if (newY + clickedShape.getHeight() > inventoryBound && newY < inventoryBound) {
                                    if (newY + clickedShape.getHeight() / 2 < inventoryBound) {
                                        newY = inventoryBound - clickedShape.getHeight() - inventoryEdge;
                                    } else {
                                        newY = inventoryBound + inventoryEdge;
                                    }
                                }
                                currentPage.updateCurShape(newX, newY);
                            }
                        }
                    }
                    invalidate();
                }
                clickedShape = null;
                insideShape = false;
        }
        return true;
    }
}

