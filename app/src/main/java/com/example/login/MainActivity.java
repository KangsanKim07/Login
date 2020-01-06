package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String in_student_id;
    String in_password;
    String right_student_id;
    String right_password;
    String name;
    String SERVER ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //클릭하면 사용자 입력 정보 in 으로 받아옴
                SERVER = "http://192.249.19.252:1780/logins/";
                EditText id = findViewById(R.id.editText2);
                EditText pw = findViewById(R.id.editText3);
                in_student_id = id.getText().toString();
                in_password = pw.getText().toString();
                SERVER = SERVER + in_student_id;
                //서버에 id 요청
                HttpGetRequest request = new HttpGetRequest();
                request.execute();

            }
        });
    }

    // 웹서버에서 사용자 수강과목 JSONArray 데이터 가져와주는 클래스
    public class HttpGetRequest extends AsyncTask<Void, Void, String> {

        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(Void... params){
            String op;
            String inputLine;

            try {
                // connect to the server
                URL myUrl = new URL(SERVER);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                Log.d("t", "------------------------------" + SERVER);
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                // get the string from the input stream
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                op = stringBuilder.toString();

            } catch(IOException e) {
                e.printStackTrace();
                op = "error";
            }

            return op;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
//            TextView textView = (TextView)findViewById(R.id.textView);
//            textView.setText(result);
            try {
                //서버에 해당 id 찾아서 result 받아오고 파싱
                JSONArray jsonArray = new JSONArray(result);
                right_student_id = jsonArray.getJSONObject(0).getString("student_id");
                right_password = jsonArray.getJSONObject(0).getString("password");
                if(right_password.equals(in_password)){
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    name = jsonArray.getJSONObject(0).getString("name");
                }
                else Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();

            } catch (JSONException e){
                //핸들해줘요
            }
        }

    }

}
