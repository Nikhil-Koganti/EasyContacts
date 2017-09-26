package com.nikhilkoganti.easycontacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Button feedbackButton = (Button) findViewById(R.id.feedback_button);
        EditText feedbackText = (EditText) findViewById(R.id.feedback_edittext);
        final TextView wordsLeftTextView = (TextView) findViewById(R.id.words_left_text_view);

        wordsLeftTextView.setText(String.format(Locale.US, getString(R.string.words_left), 100));

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                feedbackText.getText().toString();

                Toast.makeText(getApplicationContext(), "Thank you for the feedback", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        feedbackText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = s.toString();

                int spaces = str == null ? 0 : str.length() - str.replace(" ", "").length();
                wordsLeftTextView.setText(String.format(Locale.US, getString(R.string.words_left), 100 - spaces - 1));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });



    }
}
