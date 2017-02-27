package comp2601.carleton.edu.comp2601a2client;

/**
 * Created by maximkuzmenko on 2017-02-22.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static comp2601.carleton.edu.comp2601a2client.R.string.gameInProgress;
import static comp2601.carleton.edu.comp2601a2client.R.string.mainThreadException;
import static comp2601.carleton.edu.comp2601a2client.R.string.neutralgame;
import static comp2601.carleton.edu.comp2601a2client.R.string.running;
import static comp2601.carleton.edu.comp2601a2client.R.string.startText;
import static comp2601.carleton.edu.comp2601a2client.R.string.userloss;
import static comp2601.carleton.edu.comp2601a2client.R.string.userwin;

//UI updates must occur through methods defined in the MainActivity class.

public class GameActivity extends AppCompatActivity {
    private ImageButton[] XObuttons;
    private Button start;
    private EditText editText;
    private int lastMove; //0 if player did the last move, 1 if computer did the last move
    private int temp;
    private static boolean threadActive; //if thread is active, used in the Thread in this class, in Game.isAllFilled and in the click listener
    public static ArrayList<Integer> XMoves;
    public static ArrayList<Integer> OMoves;
    public static ArrayList<Integer> XOMoves; //Will be  full {0,1,2,3,4,5,6,7,8}
    private int gameResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameactivityview);

        final boolean isFirst = MainActivity.getInstance().isFirst;

        XMoves = new ArrayList<Integer>(); //Stores moves made by player
        OMoves = new ArrayList<Integer>(); //Stores moves made by Computer
        XOMoves = new ArrayList<Integer>(); //Stores remaining available moves (buttons that have not been selected)

        XObuttons = new ImageButton[9];

        for (int i = 0; i < 9; i++) { //findViewById to all XObuttons
            XObuttons[i] = (ImageButton) findViewById(getResources().getIdentifier("imageButton" + (i+1), "id", getPackageName()));
        }

        XOButtonInitClickListeners();

        XObuttonsClickable(false); //Disables buttons until player hits "Start"

        editText = (EditText) findViewById(R.id.editText);

        start = (Button) findViewById(R.id.button10);

        if (isFirst) {
          editText.setText(startText);
        }
        else {
          editText.setText("Waiting for other player to begin...");
        }

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //do start
                if (isFirst) { //This fil list with all available buttons.
                    for(int i=0; i<9; i++){
                        XOMoves.add(i);
                    }

                    XObuttonsClickable(false);
                    //lastMove = 1; //Variable that determines who plays first (this indicates that the player starts)
                    Message msg = new Message();
                    msg.header.type = "GAME_ON";
                    MainActivity.getInstance().s.request(msg);
                    start.setText("stop");
                    editText.setText(gameInProgress);

                }
            }
        });
    }

    //rules: 1. player can only play on their turn, so if player just made a move they will have to wait until the computer makes a move before they can
    //2. if the player doesn't make a move 2 seconds after the computer makes a move, then the computer will move for the player (1st rule still applies though)
    // public synchronized void startThreadPlaying() {
    //     new Thread() {
    //         public synchronized void run() {
    //             try {
    //                 while (threadActive) {
    //                     if (lastMove == 0) { //If player played last, disable all buttons
    //                         XObuttonsClickable(false);
    //                     }
    //                     else {
    //                         //System.out.println("YOU CAN CLICK NOW");
    //                         XObuttonsClickable(true);
    //                     }
    //
    //                   //  Thread.sleep(2000);
    //
    //                     gameResult = Game.checkGameOver() ;
    //
    //                     if (gameResult != 0) { //somebody wins
    //                         endGame();
    //                         break;
    //                     }
    //
    //                   //  Random r = new Random(); //Picks a random button to click from XOMoves
    //                     temp = XOMoves.get(r.nextInt(XOMoves.size()));
    //                     // final CountDownLatch latch = new CountDownLatch(1);
    //                     // runOnUiThread(new Runnable() {
    //                     //     @Override
    //                     //     public synchronized void run() {
    //                     //         XObuttons[temp].performClick();
    //                     //         latch.countDown();
    //                     //     }
    //                     // });
    //                     // try {
    //                     //     latch.await();
    //                     // } catch (InterruptedException e) {
    //                     //     e.printStackTrace();
    //                     // }
    //                 }
    //             } catch (Exception e) {
    //                 System.out.println(mainThreadException);
    //                 e.printStackTrace();
    //             }
    //         }
    //     }.start();
    // }

    public void XObuttonsClickable(boolean val) {
        if (!val) {
            //System.out.println("Clicking is FALSE");
            for (int i = 0; i < 9; i++) { //Disable all buttons
                XObuttons[i].setClickable(false);
            }
        }
        else {
          //  System.out.println("Clicking is TRUE");
            for (int i : XOMoves) { //Enable all buttons
                System.out.println(i + " is i");
                XObuttons[i].setClickable(true);
            }
        }
    }

    public void endGame(){
        XOMoves.clear();
        XObuttonsClickable(false);
        threadActive = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                start.setText(startText);
                if (gameResult == 1) {
                    editText.setText(userwin);
                }
                else if (gameResult == 2) {
                    editText.setText(userloss);
                }
                else {
                    editText.setText(neutralgame);
                }
                for(int i=0; i<9; i++){
                    XObuttons[i].setImageResource(R.drawable.tictactoeblank); //set image resource
                    XObuttons[i].setScaleType(ImageView.ScaleType.FIT_XY); //scale to fit button
                }
            }
        });
    }

    public void makeMove(int x){
        if(lastMove == 1){
            XObuttons[x].setImageResource(R.drawable.tictactoex); //set image resource
            XObuttons[x].setScaleType(ImageView.ScaleType.FIT_XY); //scale to fit button
            lastMove = 0;
            XMoves.add(x);
            XObuttonsClickable(false);
        }
        else {
            XObuttons[x].setImageResource(R.drawable.tictactoeo); //set image resource
            XObuttons[x].setScaleType(ImageView.ScaleType.FIT_XY); //scale to fit button
            lastMove = 1;
            OMoves.add(x);
        }
       // System.out.println("Removing " + Integer.valueOf(x));
        editText.setText("Button " + x + " Pressed");
        XObuttons[x].setClickable(false); //make button unclickable after
        XOMoves.remove(Integer.valueOf(x));
    }

    public void XOButtonInitClickListeners() {
        XObuttons[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(0);
            }
        });

        XObuttons[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(1);
            }
        });

        XObuttons[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(2);
            }
        });

        XObuttons[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(3);
            }
        });

        XObuttons[4].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(4);
            }
        });

        XObuttons[5].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(5);
            }
        });

        XObuttons[6].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(6);
            }
        });

        XObuttons[7].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(7);
            }
        });

        XObuttons[8].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeMove(8);
            }
        });
    }
}
