package com.club.management.clubmanage.OL;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.club.management.clubmanage.CORE.NetworkTask;
import com.club.management.clubmanage.R;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Kyeongmin on 2017-06-05.
 */
public class RegisterActivity_2 extends AppCompatActivity {
    EditText userid, userpw, usernm, Email;
    TextView nowResult;
    CheckedTextView usermale,userfemale;
    Button IdCheckBtn ,userBirthBtn ,Send2;
    String userId, userPw, userNm, userBirth, email, sexCd = null;
    boolean idChecked, inputId = false;

    NetworkTask networkTask = new NetworkTask();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        nowResult = (TextView) findViewById(R.id.nowresult);
        userid = (EditText) findViewById(R.id.userid);
        userpw = (EditText) findViewById(R.id.userpw);
        usernm = (EditText) findViewById(R.id.usernm);
        Email = (EditText) findViewById(R.id.Email);
        usermale = (CheckedTextView) findViewById(R.id.usermale);
        userfemale = (CheckedTextView) findViewById(R.id.userFemale);
        IdCheckBtn = (Button) findViewById(R.id.idCheckBtn);
        userBirthBtn = (Button) findViewById(R.id.userBirthBtn);
        Send2 = (Button) findViewById(R.id.send2);

        Intent intent =getIntent();
        Map<String, Object> userInfo = (Map<String, Object>) intent.getSerializableExtra("userInfo");

        IdCheckBtn.setClickable(false);

        // 아이디 입력 변경시 이벤트
        userid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputId =true;
                if(Pattern.matches("^[a-zA-Z0-9]*$",userid.getText().toString())){
                    if(s.length()<5){
                        nowResult.setText("5자리 이상이여야 됩니다");
                    }else if(s.length()>10){
                        nowResult.setText("10자리 이하여야 됩니다");
                    }else{
                        idChecked =true;
                        IdCheckBtn.setClickable(true);
                        nowResult.setText("중복검사를 해주세요");
                    }
                }else{
                    nowResult.setText("영어,숫자형식이 필요합니다");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 중복검사 후 다시 포커스를 가질때
        userid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && idChecked){
                    IdCheckBtn.setBackgroundColor(Color.BLACK);
                    idChecked=false;
                }
            }
        });

        // ID체크 버튼
       IdCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputId==true) {
                    userId = userid.getText().toString();
                    IdCheckBtn.setBackgroundColor(getResources().getColor(R.color.colorholoblue));
                    nowResult.setText("중복 검사 완료");
                    userpw.requestFocus();
                    userpw.setCursorVisible(true);
                    // 중복체크

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("url", "/ol/chkDupUserInfo.do");
                        params.put("userId", userId);

                        try {
                            // db로 전송 networkTask.execute(params);
                            // .get();  String으로 return 값 받기
                            String result = (networkTask.execute(params)).get();

                            JSONObject jsonObject = new JSONObject(result);

                            int rtnCode = jsonObject.getInt("rtnCode");
                            if (rtnCode == 0) {
                                IdCheckBtn.setBackgroundColor(getResources().getColor(R.color.colorholoblue));
                                nowResult.setText("중복 검사 완료");
                                idChecked = true;
                                userpw.requestFocus();
                                userpw.setCursorVisible(true);
                            } else {
                                String rtnMsg = jsonObject.getString("rtnMsg");
                                Toast.makeText(getApplicationContext(), rtnMsg, Toast.LENGTH_LONG).show();
                                IdCheckBtn.setBackgroundColor(Color.BLACK);
                                idChecked=false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                }
            }
        });
        //성별 남 체크
        usermale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usermale.setChecked(true);
                userfemale.setChecked(false);
                userfemale.setBackgroundColor(Color.WHITE);
                usermale.setBackgroundColor(getResources().getColor(R.color.colorholoblue));
                sexCd = "M";
            }
        });
        //성별 여 체크
        userfemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userfemale.setChecked(true);
                usermale.setChecked(false);
                usermale.setBackgroundColor(Color.WHITE);
                userfemale.setBackgroundColor(getResources().getColor(R.color.colorholoblue));
                sexCd = "W";
            }
        });

        userBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =new DatePickerDialog(RegisterActivity_2.this,listener,1993,5,31);
                datePickerDialog.show();
            }
        });

        Send2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userId = userid.getText().toString();
                userPw = userpw.getText().toString();
                userNm = usernm.getText().toString();
                email = Email.getText().toString();

                if (userId.replace(" ","").equals("") || userPw.replace(" ","").equals("") || userNm.replace(" ","").equals("") || userBirth == null || email.replace(" ","").equals("") || sexCd == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity_2.this);
                    builder.setTitle("입력 오류")
                            .setMessage("모두 입력해야 됩니다")
                            .setCancelable(true)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //원하는 클릭 이벤트를 넣으시면 됩니다.
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(idChecked == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity_2.this);
                    builder.setTitle("오류")
                            .setMessage("아이디 중복검사를 해주세요")
                            .setCancelable(true)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //원하는 클릭 이벤트를 넣으시면 됩니다.
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity_2.this);
                    builder.setTitle("이메일 오류")
                            .setMessage("이메일 형식이 맞지 않습니다")
                            .setCancelable(true)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Email.setText("");
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity_3.class);
                    Map<String, Object> userInfo = new HashMap<String, Object>();
                    userInfo.put("userId", userId);
                    userInfo.put("userPw", userPw);
                    userInfo.put("userNm", userNm);
                    userInfo.put("userBirth", userBirth);
                    userInfo.put("email", email);
                    userInfo.put("sexCd", sexCd);

                    intent.putExtra("userInfo", (Serializable) userInfo);

                    startActivity(intent);
                }
            }
        });
    }
    //날짜 띄우기
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            userBirth = String.format(String.valueOf(year)+ "-" +String.valueOf(monthOfYear+1)+ "-"+String.valueOf(dayOfMonth));
            userBirthBtn.setText(userBirth);
        }
    };
}
