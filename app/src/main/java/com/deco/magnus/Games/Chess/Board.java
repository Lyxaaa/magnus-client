package com.deco.magnus.Games.Chess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.deco.magnus.ProjectNet.Messages.BoardResult;
import com.deco.magnus.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public static final int SIZE = WIDTH * HEIGHT;

    private int dark;
    private int darker;
    private int light;
    private int white;

    private Object lock = new Object();

    //region Interaction Listener
    public interface OnInteractListener {
        void OnInteract(ChessPiece piece, String destination);
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
    ChessPiece[] state = new ChessPiece[SIZE];
    //endregion

    //region Rendering
    final Activity activity;
    ImageView[][] squares = new ImageView[8][8];
    HashMap<ChessPiece, Integer> pieceResources = new HashMap<ChessPiece, Integer>() {{
        put(ChessPiece.WhitePawn, R.drawable.wp);
        put(ChessPiece.WhiteRook, R.drawable.wr);
        put(ChessPiece.WhiteKnight, R.drawable.wn);
        put(ChessPiece.WhiteBishop, R.drawable.wb);
        put(ChessPiece.WhiteKing, R.drawable.wk);
        put(ChessPiece.WhiteQueen, R.drawable.wq);

        put(ChessPiece.BlackPawn, R.drawable.bp);
        put(ChessPiece.BlackRook, R.drawable.br);
        put(ChessPiece.BlackKnight, R.drawable.bn);
        put(ChessPiece.BlackBishop, R.drawable.bb);
        put(ChessPiece.BlackKing, R.drawable.bk);
        put(ChessPiece.BlackQueen, R.drawable.bq);
    }};
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
    static public float EDGEWEIGHT = 0;
    static public float SQUAREWEIGHT = 1;
    //endregion

    @SuppressLint("ClickableViewAccessibility")
    public Board(Activity a, final TableLayout table, int fontSize) {
        if (fontSize > 0) {
            FONTSIZE = fontSize;
        }

        dark = a.getResources().getColor(R.color.dark);
        darker = a.getResources().getColor(R.color.darker);
        light = a.getResources().getColor(R.color.light);
        white = a.getResources().getColor(R.color.white);

        activity = a;
        table.setGravity(Gravity.CENTER);
        table.setBackgroundColor(darker);

        View topLeft = null;
        View botRight = null;

        for (int i = 0; i < 10; i++) {
            TableRow row = new TableRow(activity);
            table.addView(row);

            //region Row Formatting
            row.setLayoutParams(
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            i == 0 || i == 9 ? FONTSIZE * 4 : TableRow.LayoutParams.MATCH_PARENT,
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
                                j == 0 || j == 9 ? FONTSIZE * 4 : TableRow.LayoutParams.MATCH_PARENT,
                                j == 0 || j == 9 ? FONTSIZE * 4 : TableRow.LayoutParams.MATCH_PARENT,
                                j == 0 || j == 9 ? EDGEWEIGHT : SQUAREWEIGHT
                        )
                );
                square.setBackgroundColor(i == 0 || i == 9 || j == 0 || j == 9 ?
                        darker : getColourOfSquare(i, j));
                //endregion
                //region Squares and Labels
                if ((i == 0 || i == 9) && (j > 0 && j < 9)) {
                    TextView tv = new TextView(activity);
                    square.addView(tv);
                    tv.setTextSize(FONTSIZE);
                    tv.setHeight(FONTSIZE * 3);
                    tv.setHeight(FONTSIZE * 3);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(white);
                    tv.setText(LETTERS[j - 1]);
                } else if ((j == 0 || j == 9) && (i > 0 && i < 9)) {
                    FrameLayout fl = new FrameLayout(activity);
                    TextView tv = new TextView(activity);
                    square.addView(tv);
                    tv.setTextSize(FONTSIZE);

                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(white);
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

    //clears the board and replaces all pieces based on hashmaps
    public void reRender() {
        for (ImageView[] squareRow : squares) {
            for (ImageView square : squareRow) {
                square.setImageResource(0);
                square.refreshDrawableState();
            }
        }

        for (int i = 0; i < state.length; i++) {
            if (state[i] != ChessPiece.None) {
                Point p = indexToPoint(i);
                ImageView square = squares[p.y][p.x];
                square.setImageResource(pieceResources.get(state[i]));
            }
        }
    }

    public void forceUpdate(ChessPiece[] board) {
        synchronized (lock){
            System.arraycopy(board, 0, state, 0, state.length);
        }
        reRender();
    }

    public void setStateFromNetworkMessage(BoardResult result) {
        String[] squares = result.board.split(", ");
        ChessPiece[] board = new ChessPiece[state.length];
        for(int i = 0; i < state.length; i++) {
            board[i] = ChessPiece.fromInt(Integer.parseInt(squares[i]));
        }
        forceUpdate(board);
    }

    public String getStateForSending( ) {
        synchronized (lock){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < state.length; i ++) {
                sb.append(state[i].toString()).append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        }
    }

    public static Point indexToPoint(int index) {
        if (index >= 0 && index < SIZE) {
            return new Point(index % WIDTH, index / WIDTH);
        }
        return null;
    }


    //moves piece from location to destination
    public void movePiece(Point src, Point dst) {
        squares[dst.x][dst.y].setImageDrawable(squares[src.x][src.y].getDrawable());
        squares[src.x][src.y].setImageResource(0);
    }

    // gets if a square should be black or white
    int getColourOfSquare(int i, int j) {
        if (i % 2 == j % 2) return light;
        else return dark;
    }

    // gets the official name of the square
    String getNameOfSquare(int i, int j) {
        if (i < 0 || i > LETTERS.length) return null;
        if (j < 0 || j > NUMBERS.length) return null;
        return LETTERS[i] + NUMBERS[j];
    }

    public enum ChessPiece {
        None(0),

        WhitePawn(1),

        WhiteRook(2),
        WhiteKnight(3),
        WhiteBishop(4),

        WhiteKing(5),
        WhiteQueen(6),

        BlackPawn(7),

        BlackRook(8),
        BlackKnight(9),
        BlackBishop(10),

        BlackKing(11),
        BlackQueen(12);

        private int value;

        private static final SparseArray<ChessPiece> intToTypeMap = new SparseArray<>();

        static {
            for (ChessPiece type : ChessPiece.values()) {
                intToTypeMap.put(type.value, type);
            }
        }

        public static ChessPiece fromInt(int i) {
            ChessPiece type = intToTypeMap.get(i);
            if (type == null)
                return ChessPiece.None;
            return type;
        }

        ChessPiece(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @NonNull
        @Override
        public String toString() {
            return Integer.toString(this.value) ;
        }
    }
}
