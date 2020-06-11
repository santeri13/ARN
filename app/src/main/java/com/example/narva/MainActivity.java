package com.example.narva;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button button, Register,Anonymous;
    private EditText email,password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mfirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        hideSoftKeyboard();
        hideNavigationBan();
        button = findViewById(R.id.button2);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editText4);
        password = findViewById(R.id.editText5);
        mfirebaseAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    Intent i = new Intent(MainActivity.this, nav.class);
                    startActivity(i);
                }
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                if(Email.isEmpty()){
                    email.setError("Please enter email id");
                    email.requestFocus();
                }
                else  if(Password.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else  if(Email.isEmpty() && Password.isEmpty()){
                    Toast.makeText(MainActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(Email.isEmpty() && Password.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Login Error, Please Login Again",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent intToHome = new Intent(MainActivity.this,nav.class);
                                startActivity(intToHome);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                }
            }
        });
        Register = findViewById(R.id.buttonRegister);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });
        Anonymous = findViewById(R.id.Anonymous);
        Anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signInAnonymously().
                        addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            openSlider();
                        }
                    }
                });
            }
        });


    }
    public void openSlider(){
        Intent intent = new Intent(this, Slider.class);
        startActivity(intent);
    }
    public void openRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mfirebaseAuth != null ){
            FirebaseAuth.getInstance().removeAuthStateListener(mfirebaseAuth);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        hideNavigationBan();
        firebaseAuth.addAuthStateListener(mfirebaseAuth);
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideNavigationBan();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        hideNavigationBan();
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBan();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideNavigationBan();
    }

    public void hideNavigationBan(){
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

}
