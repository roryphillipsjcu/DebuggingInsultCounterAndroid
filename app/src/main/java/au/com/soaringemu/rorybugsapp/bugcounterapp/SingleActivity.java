package au.com.soaringemu.rorybugsapp.bugcounterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SingleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        counterSocket.on("updateBugCount", onUpdateBugCounter);
        counterSocket.connect();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        counterSocket.disconnect();
        counterSocket.off("updateBugCount");
    }

    //View
    private void updateBugCounterView(int bugCount){
        TextView countTextView = (TextView) findViewById(R.id.countText);
        countTextView.setText("Count: " + Integer.toString(bugCount));
    }

    public void bugButtonPress(View view) {
        Log.d("Activity", "BugButtonPressed");
        sendBugButtonPress();
    }

    //Socket.io Listeners
    private Emitter.Listener onUpdateBugCounter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int bugCount;
                    try {
                        bugCount = data.getInt("bugCount");
                    } catch (JSONException e){
                        return;
                    }

                    updateBugCounterView(bugCount);
                }
            });
        }
    };

    //Socket.io Emitters
    public void sendBugButtonPress(){
        counterSocket.emit("bugButtonPress");
    }

    //Socket.io socket
    private Socket counterSocket;
    {
        try {
            counterSocket = IO.socket("http://144.138.183.34:4500");
        } catch (URISyntaxException e){

        }
    }


}
