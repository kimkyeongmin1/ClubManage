package com.club.management.clubmanage.OL;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.club.management.clubmanage.CORE.NetworkTask;
import com.club.management.clubmanage.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kyeongmin on 2017-06-05.
 */

public class RegisterActivity_3 extends AppCompatActivity {
    Button Send3;
    ListView Local;
    ListView Univ;
    List nowLocalCd = new ArrayList();
    List nowLocalNm = new ArrayList();
    List nowUnivNm = new ArrayList();
    List nowUnivCd = new ArrayList();

    Map<String, String> userInfo = new HashMap<String, String>();
    String univCd;
    String userUniv;
    String phoneSt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_3);

        Send3 = (Button) findViewById(R.id.send3);
        Local = (ListView) findViewById(R.id.local);
        Univ = (ListView) findViewById(R.id.univ);

        Intent intent =getIntent();
        userInfo =  (Map<String, String>) intent.getSerializableExtra("userInfo");

        TelephonyManager telephonyManager =(TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        phoneSt = telephonyManager.getLine1Number().concat("_").concat(Build.DEVICE);

        init();

        ArrayAdapter localAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nowLocalNm);
        Local.setAdapter(localAdapter);

        //지역리스트 선택
        Local.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUniversity(position);
            }
        });

        Send3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity_3.class);
                userInfo.put("univCd", univCd);
                userInfo.put("phoneSt",phoneSt);

                Map<String, String> params = new HashMap<String, String>();
                params.put("url", "/ol/saveUserInfo.do");
                params.put("saveTp", "N");
                params.putAll(userInfo);

                try {
                    NetworkTask networkTask = new NetworkTask();
                    networkTask.execute(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent newintent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(newintent);
            }
        });
    }
    //초기화면 지역띄우기
    void init() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", "/co/selectCodeList.do");
        params.put("lrgCd", "COMMON");
        params.put("midCd", "LOC_CD");

        try {
            NetworkTask networkTask = new NetworkTask();

            String result = (networkTask.execute(params)).get();

            JSONObject jsonObject = new JSONObject(result);

            JSONArray selectCodeList = jsonObject.getJSONArray("selectCodeList");

            String locCd = "";
            String locNm = "";
            JSONObject locInfo = null;
            for (int i = 0; i < selectCodeList.length(); i++) {
                locInfo = selectCodeList.getJSONObject(i);

                locCd = (String) locInfo.get("SML_CD");
                locNm = (String) locInfo.get("CD_NM");

                nowLocalNm.add(locNm);
                nowLocalCd.add(locCd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //지역에 맞는 대학교
    void showUniversity(int index) {
        String locCd = (String) nowLocalCd.get(index);
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", "/om/selectUnivList.do");
        params.put("locCd", locCd);

        try {
            NetworkTask networkTask = new NetworkTask();
            String result = (networkTask.execute(params)).get();
            JSONObject jsonObject = new JSONObject(result);

            JSONArray univList = jsonObject.getJSONArray("unviList");

            String univCd = "";
            String univNm = "";
            JSONObject univInfo = null;
            for (int i = 0; i < univList.length(); i++) {
                univInfo = univList.getJSONObject(i);

                univCd = (String) univInfo.get("UNIV_CD");
                univNm = (String) univInfo.get("UNIV_NM");

                nowUnivCd.add(univCd);
                nowUnivNm.add(univNm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //대학교 선택
    void connectUniversity(){
        ArrayAdapter univAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nowUnivNm);
        Log.d("Arrayadapter","Arrayadapter");
        Univ.setAdapter(univAdapter);

        Univ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                univCd =(String) nowUnivCd.get(position);
                userUniv = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), userUniv+"를 선택하셨습니다", Toast.LENGTH_LONG).show();
            }
        });
    }
}
