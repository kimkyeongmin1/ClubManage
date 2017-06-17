package com.club.management.clubmanage.OL;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.club.management.clubmanage.CORE.JsonUtil;
import com.club.management.clubmanage.CORE.NetworkTask;
import com.club.management.clubmanage.MainActivity;
import com.club.management.clubmanage.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kyeongmin on 2017-06-11.
 */

public class LoginActivity extends AppCompatActivity {
    EditText userid;
    EditText userpw;
    Button loginBtn;
    Button registerBtn;
    CheckBox chkAutoLogin;
    String phoneSt;

    // 자동로그인 저장
    SharedPreferences _userInfo;
    SharedPreferences.Editor editor;

    String userId;
    String userPw;
    String _userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userid = (EditText)findViewById(R.id.userid);
        userpw = (EditText) findViewById(R.id.userpw);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.regsterBtn);
        chkAutoLogin = (CheckBox) findViewById(R.id.chkAutoLogin);

        TelephonyManager telephonyManager =(TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        phoneSt = telephonyManager.getLine1Number().concat("_").concat(Build.DEVICE);

        _userInfo = getSharedPreferences("_userInfo", 0);
        editor= _userInfo.edit();

        if(_userInfo.getBoolean("Auto_Login_enabled", false)){
            userid.setText(_userInfo.getString("_userId", ""));
            userpw.setText(_userInfo.getString("_userPw", ""));
            chkAutoLogin.setChecked(true);
        }

        // 로그인
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = userid.getText().toString();
                userPw = userpw.getText().toString();

                if(chkAutoLogin.isChecked()){
                    editor.putString("_userId", userId);
                    editor.putString("_userPw", userPw);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                }else{
                    editor.clear();
                    editor.commit();
                }

                NetworkTask networkTask = new NetworkTask();

                Map<String, String> params = new HashMap<String, String>();
                params.put("url", "/co/procLogin.do");
                params.put("userId", userId);
                params.put("userPw", userPw);
                params.put("phoneSt",phoneSt);

                try {
                    String result = (networkTask.execute(params)).get();

                    JSONObject jsonObject = new JSONObject(result);

                    int rtnCode = jsonObject.getInt("rtnCode");

                    if(rtnCode != 0 ){
                        String rtnMsg = jsonObject.getString("rtnMsg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("중복 로그인")
                                .setMessage(rtnMsg)
                                .setCancelable(true)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    //새로운 기기 등록 및 로그인
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NetworkTask networkTask = new NetworkTask();
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("url", "/co/procLogin.do");
                                        params.put("userId", userId);
                                        params.put("userPw", userPw);
                                        params.put("phoneSt",phoneSt);
                                        params.put("forceLogin","Y");
                                        try {
                                            networkTask.execute(params).get();
                                    }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        return;
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    // 로그인 성공시 사용자 ID 세션 등록


                    JSONObject userArray = jsonObject.getJSONObject("userInfo");
                    Map<String, Object> userInfo = JsonUtil.toMap(jsonObject.getJSONObject("userInfo"));
                    Log.d("userInfo",userInfo.toString());

                    Log.d("_userid", (String) userInfo.get("USER_ID"));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("_userId",_userId);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 회원가입
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity_1.class);
                startActivity(intent);
            }
        });

        //자동 로그인시
        chkAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString("_userId", userId);
                    editor.putString("_userPw", userPw);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }
}
