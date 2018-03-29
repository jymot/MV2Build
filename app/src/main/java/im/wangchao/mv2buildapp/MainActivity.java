package im.wangchao.mv2buildapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meituan.android.walle.ChannelInfo;

import im.wangchao.buildhelper.ApkInfoReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.testbtn);
        final TextView textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readChannel(textView);
            }
        });

        Bundle meta = getApplicationInfo().metaData;
        if (meta != null){
            Log.e("wcwcwc", "Test:" + getApplicationInfo().metaData.getString("Test"));
        }
    }
    private void readChannel(TextView textView) {
        final long startTime = System.currentTimeMillis();
        final ChannelInfo channelInfo = ApkInfoReader.getChannelInfo(this.getApplicationContext());
        if (channelInfo != null) {
            textView.setText(channelInfo.getChannel() + "\n" + channelInfo.getExtraInfo());
        }
        Toast.makeText(this, "ChannelReader takes " + (System.currentTimeMillis() - startTime) + " milliseconds", Toast.LENGTH_SHORT).show();
    }
}
