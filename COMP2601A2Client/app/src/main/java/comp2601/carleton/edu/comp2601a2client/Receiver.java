package comp2601.carleton.edu.comp2601a2client;

/**
 * Created by tonywhite on 2017-01-28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MessageService.CUSTOM_INTENT.equals(intent.getAction())) {
            Message data = (Message) intent.getSerializableExtra("myKey");
            System.out.println("Message in Receiver: " + data.header.type);
            if (data.header.type.equals("CONNECT_RESPONSE")) {
               MainActivity.getInstance().showConnectedToast();
                MainActivity.getInstance().dialog1.dismiss();
            }
            else if (data.header.type.equals("USERS_UPDATED")) { //Updates list of users currently connected
                MainActivity.getInstance().updateList((ArrayList<String>) data.body.getMap().get("userList"));
            }
            else if (data.header.type.equals("PLAY_GAME_REQUEST")) { //display yes/no modal dialog, send response back to server
                MainActivity.getInstance().playGameRequest(data.header.recipient);
            }
            else if (data.header.type.equals("PLAY_GAME_RESPONSE")) { //if play yes, start new game, else show on textview that X does not want to play
                MainActivity.getInstance().playGameResponse(data.header.recipient,data.header.play);
            }
            else if (data.header.type.equals("DISCONNECT_RESPONSE")) { //disconnect and quit the app

            }
            else if (data.header.type.equals("GAME_ON")) { //button changes to Stop and the text view should contain the text, "X has started a game.". Here X is the name of the opponent. All tic-tac-toe buttons become clickable for the player whose turn it is.

            }
            else if (data.header.type.equals("MOVE_MESSAGE")) { //When a move is made, and the move results in a win, loss or tie, the player receiving the MOVE_MESSAGE must send a GAME_OVER message to the player making the move. The button changes to "Start" and the text view should contain the text: "I won the game.", "X won the game." or "The game was a draw."

            }
        }
    }
}
