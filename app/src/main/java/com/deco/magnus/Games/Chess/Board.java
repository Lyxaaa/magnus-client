package com.deco.magnus.Games.Chess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.deco.magnus.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    //region Interaction Listener
    public interface OnInteractListener {
        void OnInteract(Piece piece, String destination);
    }

    ArrayList<OnInteractListener> interactListeners = new ArrayList<>();

    public void addOnInteractListener(OnInteractListener listener) {
        interactListeners.add(listener);
    }

    public void removeOnInteractListener(OnInteractListener listener) {
        interactListeners.remove(listener);
    }

    //endregion
    //region Board State
    HashMap<PieceType, Point> blackPieces = new HashMap<>();
    HashMap<PieceType, Point> whitePieces = new HashMap<>();

    HashMap<PieceType, Point> blackPiecesDefault = new HashMap<PieceType, Point>() {{
        put(PieceType.Pawn1, new Point(0, 1));
        put(PieceType.Pawn2, new Point(1, 1));
        put(PieceType.Pawn3, new Point(2, 1));
        put(PieceType.Pawn4, new Point(3, 1));
        put(PieceType.Pawn5, new Point(4, 1));
        put(PieceType.Pawn6, new Point(5, 1));
        put(PieceType.Pawn7, new Point(6, 1));
        put(PieceType.Pawn8, new Point(7, 1));
        put(PieceType.RookK, new Point(0, 0));
        put(PieceType.KnightK, new Point(1, 0));
        put(PieceType.BishopK, new Point(2, 0));
        put(PieceType.King, new Point(3, 0));
        put(PieceType.Queen, new Point(4, 0));
        put(PieceType.BishopQ, new Point(5, 0));
        put(PieceType.KnightQ, new Point(6, 0));
        put(PieceType.RookQ, new Point(7, 0));
    }};
    HashMap<PieceType, Point> whitePiecesDefault = new HashMap<PieceType, Point>() {{
        put(PieceType.Pawn1, new Point(0, 6));
        put(PieceType.Pawn2, new Point(1, 6));
        put(PieceType.Pawn3, new Point(2, 6));
        put(PieceType.Pawn4, new Point(3, 6));
        put(PieceType.Pawn5, new Point(4, 6));
        put(PieceType.Pawn6, new Point(5, 6));
        put(PieceType.Pawn7, new Point(6, 6));
        put(PieceType.Pawn8, new Point(7, 6));
        put(PieceType.RookK, new Point(0, 7));
        put(PieceType.KnightK, new Point(1, 7));
        put(PieceType.BishopK, new Point(2, 7));
        put(PieceType.King, new Point(3, 7));
        put(PieceType.Queen, new Point(4, 7));
        put(PieceType.BishopQ, new Point(5, 7));
        put(PieceType.KnightQ, new Point(6, 7));
        put(PieceType.RookQ, new Point(7, 7));
    }};
    //endregion

    //region Rendering
    final Activity activity;
    ImageView[][] squares = new ImageView[8][8];
    HashMap<PieceType, Integer> blackPieceResources = new HashMap<PieceType, Integer>() {{
        put(PieceType.Pawn1, R.drawable.bp);
        put(PieceType.Pawn2, R.drawable.bp);
        put(PieceType.Pawn3, R.drawable.bp);
        put(PieceType.Pawn4, R.drawable.bp);
        put(PieceType.Pawn5, R.drawable.bp);
        put(PieceType.Pawn6, R.drawable.bp);
        put(PieceType.Pawn7, R.drawable.bp);
        put(PieceType.Pawn8, R.drawable.bp);
        put(PieceType.RookK, R.drawable.br);
        put(PieceType.KnightK, R.drawable.bn);
        put(PieceType.BishopK, R.drawable.bb);
        put(PieceType.King, R.drawable.bk);
        put(PieceType.Queen, R.drawable.bq);
        put(PieceType.BishopQ, R.drawable.bb);
        put(PieceType.KnightQ, R.drawable.bn);
        put(PieceType.RookQ, R.drawable.br);
    }};
    HashMap<PieceType, Integer> whitePieceResources = new HashMap<PieceType, Integer>() {{
        put(PieceType.Pawn1,   R.drawable.wp);
        put(PieceType.Pawn2,   R.drawable.wp);
        put(PieceType.Pawn3,   R.drawable.wp);
        put(PieceType.Pawn4,   R.drawable.wp);
        put(PieceType.Pawn5,   R.drawable.wp);
        put(PieceType.Pawn6,   R.drawable.wp);
        put(PieceType.Pawn7,   R.drawable.wp);
        put(PieceType.Pawn8,   R.drawable.wp);
        put(PieceType.RookK,   R.drawable.wr);
        put(PieceType.KnightK, R.drawable.wn);
        put(PieceType.BishopK, R.drawable.wb);
        put(PieceType.King,    R.drawable.wk);
        put(PieceType.Queen,   R.drawable.wq);
        put(PieceType.BishopQ, R.drawable.wb);
        put(PieceType.KnightQ, R.drawable.wn);
        put(PieceType.RookQ,   R.drawable.wr);
    }};
    private static final float[] NEGATIVE = {
            -1.0f, 0, 0, 0, 255, // red
            0, -1.0f, 0, 0, 255, // green
            0, 0, -1.0f, 0, 255, // blue
            0, 0, 0, 1.0f, 0  // alpha
    };
    Point squareSize = new Point(10, 10);
    //endregion

    //region Touch Calculations
    int[] tlOffset = new int[2];
    int width;
    int height;
    //endregion
    //region Definitions
    static public String[] LETTERS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
    static public String[] NUMBERS = new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};
    static public int FONTSIZE = 6;
    static public float EDGEWEIGHT = 1 / 18f;
    static public float SQUAREWEIGHT = 1 / 9f;
    //endregion

    @SuppressLint("ClickableViewAccessibility")
    public Board(Activity a, final TableLayout table) {
        activity = a;
        table.setGravity(Gravity.CENTER);

        View topLeft = null;
        View botRight = null;

        for (int i = 0; i < 10; i++) {
            TableRow row = new TableRow(activity);
            table.addView(row);

            //region Row Formatting
            row.setLayoutParams(
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT,
                            i == 0 || i == 9 ? EDGEWEIGHT : SQUAREWEIGHT
                    )
            );
            row.setGravity(Gravity.CENTER);
            //endregion
            for (int j = 0; j < 10; j++) {
                LinearLayout square = new LinearLayout(activity);
                row.addView(square);
                //region Format Square
                if (i == 0 && j == 0) topLeft = square;
                if (i == 9 && j == 9) botRight = square;
                square.setGravity(Gravity.CENTER);
                square.setLayoutParams(
                        new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT,
                                j == 0 || j == 9 ? EDGEWEIGHT : SQUAREWEIGHT
                        )
                );
                square.setBackgroundColor(i == 0 || i == 9 || j == 0 || j == 9 ?
                        Color.GRAY : getColourOfSquare(i, j));
                //endregion
                //region Squares and Labels
                if ((i == 0 || i == 9) && (j > 0 && j < 9)) {
                    TextView tv = new TextView(activity);
                    square.addView(tv);
                    tv.setTextSize(FONTSIZE);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(Color.WHITE);
                    tv.setText(LETTERS[j - 1]);
                } else if ((j == 0 || j == 9) && (i > 0 && i < 9)) {
                    TextView tv = new TextView(activity);
                    square.addView(tv);
                    tv.setTextSize(FONTSIZE);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(Color.WHITE);
                    tv.setText(NUMBERS[i - 1]);
                } else if (i > 0 && i < 9) {
                    ImageView iv = new ImageView(activity);
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    iv.setLayoutParams(layout);
                    layout.setMargins(20, 20, 20, 20);
                    square.addView(iv);
                    squares[i - 1][j - 1] = iv;
                }
                //endregion
            }
        }

        //region OnTouchListener
        table.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getPointerId(motionEvent.getActionIndex()) != 0) return true;
                int motion = motionEvent.getActionMasked();

                int x = (int) Math.floor((motionEvent.getX() - tlOffset[0]) / width * 8);
                int y = (int) Math.floor((motionEvent.getY() - tlOffset[1]) / height * 8);

                x = x < 0 ? 0 : x;
                x = x > 7 ? 7 : x;

                y = y < 0 ? 0 : y;
                y = y > 7 ? 7 : y;

                if (motion == MotionEvent.ACTION_DOWN || motion == MotionEvent.ACTION_POINTER_DOWN) {
                    System.out.println("Down on " + getNameOfSquare(x, y));
                }
                if (motion == MotionEvent.ACTION_UP || motion == MotionEvent.ACTION_POINTER_UP) {
                    System.out.println("Up on " + getNameOfSquare(x, y));
                }
                return true;
            }
        });
        //endregion

        final View tl = topLeft;
        final View br = botRight;

        //region TreeObserverListener for finding offsets after OnGlobalLayout
        table.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                tl.getHitRect(rect);
                tlOffset[0] = rect.width();
                tlOffset[1] = rect.height();

                br.getHitRect(rect);

                width = table.getWidth() - tlOffset[0] - rect.width();
                height = table.getHeight() - tlOffset[1] - rect.height();

                for (ImageView[] imageViews : squares) {
                    for (ImageView iv : imageViews) {
                        iv.setMaxHeight((int) (0));
                        iv.setMaxWidth((int) (0));
                        iv.setAdjustViewBounds(true);
                    }
                }
            }
        });
        //endregion
    }

    //resets the board state and rerenders
    public void reset() {
        blackPieces = (HashMap<PieceType, Point>) blackPiecesDefault.clone();
        whitePieces = (HashMap<PieceType, Point>) whitePiecesDefault.clone();
        reRender();
    }

    //clears the board and replaces all pieces based on hashmaps
    public void reRender() {
        for (ImageView[] squareRow : squares) {
            for (ImageView square : squareRow) {
                square.setImageResource(0);
            }
        }

        for (PieceType piece : blackPieces.keySet()) {
            int resource = blackPieceResources.get(piece);
            Point location = blackPieces.get(piece);
            Drawable drawable = activity.getResources().getDrawable(resource);
            squares[location.y][location.x].setImageDrawable(drawable);
        }

        for (PieceType piece : whitePieces.keySet()) {
            int resource = whitePieceResources.get(piece);
            Point location = whitePieces.get(piece);
            Drawable drawable = activity.getResources().getDrawable(resource);
            squares[location.y][location.x].setImageDrawable(drawable);
        }
    }

    // returns true if piece at location removed, otherwise false
    public boolean tryRemovePieceAt(HashMap<PieceType, Point> pieces, Point location) {
        for (PieceType piece : pieces.keySet()) {
            if (pieces.get(piece).equals(location)) {
                pieces.remove(piece);
                return true;
            }
        }
        return false;
    }

    // returns true if piece could move to location specified
    public boolean tryMovePieceTo(HashMap<PieceType, Point> pieces, Point src, Point dst) {
        for (PieceType piece : pieces.keySet()) {
            if (pieces.get(piece).equals(src)) {
                pieces.put(piece, dst);
                return true;
            }
        }
        return false;
    }

    //moves piece from location to destination
    public void movePiece(Point src, Point dst) {
        if (tryMovePieceTo(blackPieces, src, dst)) {
            tryRemovePieceAt(blackPieces, dst);
            tryRemovePieceAt(whitePieces, dst);
        } else if (tryMovePieceTo(whitePieces, src, dst)) {
            tryRemovePieceAt(whitePieces, dst);
            tryRemovePieceAt(blackPieces, dst);
        }

        squares[dst.x][dst.y].setImageDrawable(squares[src.x][src.y].getDrawable());
        squares[src.x][src.y].setImageResource(0);
    }

    // gets if a square should be black or white
    int getColourOfSquare(int i, int j) {
        if (i % 2 == j % 2) return Color.WHITE;
        else return Color.DKGRAY;
    }

    // gets the official name of the square
    String getNameOfSquare(int i, int j) {
        if (i < 0 || i > LETTERS.length) return null;
        if (j < 0 || j > NUMBERS.length) return null;
        return LETTERS[i] + NUMBERS[j];
    }

    enum Interaction {
        Down, Up
    }

    enum Colour {
        Black, White
    }

    enum PieceType {
        Pawn1,
        Pawn2,
        Pawn3,
        Pawn4,
        Pawn5,
        Pawn6,
        Pawn7,
        Pawn8,
        RookK,
        KnightK,
        BishopK,
        King,
        Queen,
        BishopQ,
        KnightQ,
        RookQ
    }

    public class Piece {
        Colour colour;
        PieceType type;
    }
}
