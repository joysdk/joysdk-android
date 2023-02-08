package com.joysdk.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.joysdk.android.JoyCallBackListener;
import com.joysdk.android.JoySDK;
import com.joysdk.android.base.JoyGameEventCode;
import com.joysdk.android.model.JoyGameInfoModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    Button btn_hall;
    Button btn_mode;
    Button btn_reset;
    EditText et_app_key;
    EditText et_token;
    GameInfoAdapter adapter;
    private ArrayList<JoyGameInfoModel> data;
    private boolean isHallMode = true;
    private static final String TAG = "JoySDKDemo";
    private String appKey = "";
    private String token = "";
    private String roomId = "";
    private String ext = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        bindView();

        bindListener();

        initSDK();
    }

    private void bindListener() {
        data = new ArrayList<>();
        adapter = new GameInfoAdapter(data, this);
        adapter.setOnItemClickListener(model -> {
            openGame(model.getGameId());
        });
        rv.setAdapter(adapter);
        rv.setVisibility(View.GONE);

        btn_hall.setOnClickListener(v -> openHall());
        btn_mode.setOnClickListener(v -> {
            if (isHallMode) {
                isHallMode = false;
                rv.setVisibility(View.VISIBLE);
                btn_hall.setVisibility(View.GONE);
                btn_mode.setText("切换到大厅模式");
            } else {
                isHallMode = true;
                rv.setVisibility(View.GONE);
                btn_hall.setVisibility(View.VISIBLE);
                btn_mode.setText("切换到列表模式");
            }
        });
        btn_reset.setOnClickListener(v -> {
            String etAppKey = et_app_key.getText().toString();
            String etToken = et_token.getText().toString();
            if (!TextUtils.isEmpty(etAppKey) && !TextUtils.isEmpty(etToken)) {
                appKey = etAppKey;
                token = etToken;
                initSDK();
                Toast.makeText(this, "重设成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "appKey或token为空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindView() {
        btn_hall = findViewById(R.id.btn_hall);
        btn_mode = findViewById(R.id.btn_mode);
        btn_reset = findViewById(R.id.btn_reset);
        et_app_key = findViewById(R.id.et_app_key);
        et_token = findViewById(R.id.et_token);
        rv = findViewById(R.id.rv);
    }

    private void log(Object obj) {
        Log.d(TAG, new Gson().toJson(obj));
    }

    private void initSDK() {
        isHallMode = true;
        btn_mode.setVisibility(View.VISIBLE);
        if (isHallMode) {
            isHallMode = false;
            rv.setVisibility(View.VISIBLE);
            btn_hall.setVisibility(View.GONE);
            btn_mode.setText("切换到大厅模式");
        } else {
            isHallMode = true;
            rv.setVisibility(View.GONE);
            btn_hall.setVisibility(View.VISIBLE);
            btn_mode.setText("切换到列表模式");
        }
        JoySDK.getInstance().init(MainActivity.this, appKey, new JoyCallBackListener.OnInitCompleteListener() {
            @Override
            public void onComplete(int code, List<JoyGameInfoModel> joyGameInfoModels) {
                //成功
                log(joyGameInfoModels);
                data.clear();
                data.addAll(joyGameInfoModels);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int code) {
                //失败
                log(code);
            }
        });
    }

    private void openHall() {
        JoySDK.getInstance().openHall(MainActivity.this, token, roomId, ext, new JoyCallBackListener.OnJoyGameEventListener() {
            @Override
            public void callback(int code, String json) {
                switch (code) {
                    case JoyGameEventCode.NEW_TPP_CLOSE:
                        //客户端收到游戏  关闭点击
                        break;
                    case JoyGameEventCode.RECHARGE:
                        //客户端收到游戏  充值点击
                        break;
                }
            }
        });
    }

    private void openGame(int gameId) {
        JoySDK.getInstance().openGame(MainActivity.this, token, gameId, roomId, ext, new JoyCallBackListener.OnJoyGameEventListener() {
            @Override
            public void callback(int code, String json) {
                switch (code) {
                    case JoyGameEventCode.NEW_TPP_CLOSE:
                        //客户端收到游戏  关闭点击
                        break;
                    case JoyGameEventCode.RECHARGE:
                        //客户端收到游戏  充值点击
                        break;
                }
            }
        });
    }

    private void refreshGameBalance() {
        JoySDK.getInstance().refreshGameBalance();
    }

    private void hideGameView() {
        JoySDK.getInstance().hideGameView();
    }
}