package com.eatuitive.nutrition;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.eatuitive.nutrition.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


/* @Created by shehab September --- November 2020*/

public class SettingActivity extends AppCompatActivity {


    SwitchCompat postSwitch;

    //used shared preferences to save the state of switch
    SharedPreferences sp;
    SharedPreferences.Editor editor;// to edit value of shared pref

    //constant for topic
    private static final String TOPIC_POST_NOTIFICATION = "POST";//assign any value but use same for this kind of notification
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //implement switch change listener
        postSwitch = findViewById(R.id.postSwitch);

        //init sp
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+TOPIC_POST_NOTIFICATION, false);
        //if enabled check switch, otherwise uncheck switch- by default unchecked/false

        if (isPostEnabled) {
            postSwitch.setChecked(true);//call to subscribe
        }else{
            postSwitch.setChecked(false);//call to subscribe
        }

        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //edit switch state
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();

                if (isChecked){
                    subscribePostNotification();
                }else{
                    unsubscribePostNotification();
                }
            }
        });
    }

    private void unsubscribePostNotification() {
        //unsubscribe to a topic (POST) to disable it's notification

        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post Notification";
                        if (!task.isSuccessful()){
                            msg = "UnSubscription failed";
                        }
                        Toast.makeText(com.eatuitive.nutrition.SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //now, in AddPostActivity when user publish post send notification with same topic(POST)

    private void subscribePostNotification() {
        //subscribe to a topic (POST) to disable it's notification
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive post Notification";
                        if (!task.isSuccessful()){
                            msg = "Subscription failed";
                        }
                        Toast.makeText(com.eatuitive.nutrition.SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
