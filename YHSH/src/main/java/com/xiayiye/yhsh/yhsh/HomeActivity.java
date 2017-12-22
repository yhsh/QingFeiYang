package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiayiye.yhsh.yhsh.fragment.FourFragment;
import com.xiayiye.yhsh.yhsh.fragment.NewsFragment;
import com.xiayiye.yhsh.yhsh.fragment.MusicFragment;
import com.xiayiye.yhsh.yhsh.fragment.MoveFragment;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getFragmentManager().beginTransaction().replace(R.id.fl_replace, new MoveFragment()).commit();
        FrameLayout fl_replace = findViewById(R.id.fl_replace);
        RadioGroup rg_bottom = findViewById(R.id.rg_bottom);
        RadioButton rb_one = findViewById(R.id.rb_move);
        RadioButton rb_two = findViewById(R.id.rb_news);
        RadioButton rb_three = findViewById(R.id.rb_three);
        RadioButton rb_four = findViewById(R.id.rb_four);
        rg_bottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment fragment;
                switch (i) {
                    case R.id.rb_move:
                        fragment = new MoveFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                    case R.id.rb_news:
                        fragment = new NewsFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                    case R.id.rb_three:
                        fragment = new MusicFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                    case R.id.rb_four:
                        fragment = new FourFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                }
            }
        });
    }
}
