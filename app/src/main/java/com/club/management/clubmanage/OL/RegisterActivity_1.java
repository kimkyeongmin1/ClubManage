package com.club.management.clubmanage.OL;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.club.management.clubmanage.CORE.NetworkTask;
import com.club.management.clubmanage.R;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.club.management.clubmanage.R.id.send1;

/**
 * Created by Kyeongmin on 2017-06-05.
 */

public class RegisterActivity_1 extends AppCompatActivity {
    //타이머 시간
    private static final int MILLISINFUTURE = 180*1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private CountDownTimer countDownTimer;

    EditText telno;
    EditText certno;
    TextView timer;
    TextView resultView;
    Button certBtn;
    Button retryBtn;
    Button Send1;
    ConstraintLayout certLayout;
    String telNo=null;
    String myPhoneNumber;
    String certNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_1);

        timer = (TextView) findViewById(R.id.timer);
        resultView = (TextView) findViewById(R.id.resultview);
        certno =(EditText) findViewById(R.id.certno);
        telno = (EditText) findViewById(R.id.telno);
        // 전화번호 입력 EditText ReadOnly 설정
        telno.setFocusable(false);
        telno.setClickable(false);
        Send1 = (Button) findViewById(send1);
        certBtn = (Button) findViewById(R.id.certBtn);
        retryBtn = (Button) findViewById(R.id.retryBtn);
        certLayout = (ConstraintLayout) findViewById(R.id.certLayout);

        // 기기 휴대폰 번호 확인 및 설정
        TelephonyManager telephonyManager =(TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        telNo = telephonyManager.getLine1Number();

        telno.setText(telNo);

        // 입력한 핸드폰 번호로 메세지 전송 및 인증
        Send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //telNo = telno.getText().toString();

                if(telNo.replace(" ","").equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity_1.this);
                    builder.setTitle("전화번호 오류")
                            .setMessage("번호를 입력하세요")
                            .setCancelable(true)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    //원하는 클릭 이벤트를 넣으시면 됩니다.
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    //인증하기 버튼 생성
                    Send1.setVisibility(View.GONE);
                    certLayout.setVisibility(View.VISIBLE);
                    insertCertNo();
                }
            }
        });

        //재전송버튼
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                resultView.setText("");
                timer.setText("");
                insertCertNo();
            }
        });

        //인증버튼
        certBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                certNo = certno.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", "/ol/chkCertNo.do");
                params.put("telNo", telNo);
                params.put("certNo",certNo);
                NetworkTask networkTask = new NetworkTask();
                try {
                    String result = (networkTask.execute(params)).get();

                    JSONObject jsonObject = new JSONObject(result);

                    int rtnCode = jsonObject.getInt("rtnCode");

                    // 인증 확인
                    if(rtnCode == 0) {
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity_2.class);
                        Map<String, Object> userInfo = new HashMap<String, Object>();
                        userInfo.put("telNo", telNo);

                        intent.putExtra("userInfo", (Serializable) userInfo);

                        startActivity(intent);
                    }else{
                        String rtnMsg = jsonObject.getString("rtnMsg");
                        resultView.setText(rtnMsg);
                        resultView.setTextColor(Color.RED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 인증번호 저장
    public void insertCertNo(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", "/ol/insertCertNo.do");
        params.put("telNo", telNo);
        NetworkTask networkTask = new NetworkTask();
        try {
            String result = (networkTask.execute(params)).get();
            JSONObject jsonObject = new JSONObject(result);
            String certNo = jsonObject.getString("certNo");

            countDownTimer();
            countDownTimer.start();

            // 메세지 전송
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(telNo, null, "인증번호는 " + certNo + "입니다.", null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //타이머 만들기
    public void countDownTimer(){
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                int current = (int) millisUntilFinished/1000;
                int min = current % (60 * 60) / 60;
                int sec = current % 60;

                timer.setText(String.valueOf(min)+":"+String.valueOf(sec));
                timer.setTextColor(Color.BLACK);
            }
            //타이머가 끝났을때
            public void onFinish() {
                resultView.setText("다시 인증해주세요");
                resultView.setTextColor(Color.RED);
                timer.setText("");

            }
        };
    }
}
