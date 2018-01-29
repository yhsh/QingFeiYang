package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SmallToolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_tools);
        final ListView home_small_tools = findViewById(R.id.home_small_tools);
        home_small_tools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        //查询火车车次
                        startActivity(new Intent(SmallToolsActivity.this,TrainSearchActivity.class));
                        break;
                }
            }
        });
    }

}
