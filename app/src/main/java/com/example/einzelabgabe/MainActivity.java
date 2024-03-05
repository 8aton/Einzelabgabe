package com.example.einzelabgabe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText inputField;
    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inputField = findViewById(R.id.matrikelnr1);
        Button send = findViewById(R.id.button1);
        resultTextView = findViewById(R.id.textView4);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputField.getText().toString();
                if (input.matches("[0-9]+")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket s = new Socket("se2-submission.aau.at", 20080);
                                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                                output.write(input);
                                output.newLine();
                                output.flush();

                                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                StringBuilder result = new StringBuilder();
                                String line;
                                while ((line = input.readLine()) != null) {
                                    result.append(line);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        resultTextView.setText(result.toString());
                                    }
                                });
                                s.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    resultTextView.setText("Bitte geben Sie nur Zahlen ein.");
                }
            }
        });
    }
}