package com.example.narva;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class nav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String field1;
    String uid;
    DatabaseReference database;
    TextView point, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav);
        hideNavigationBan();
        final DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        username = (TextView)headerView.findViewById(R.id.username);
        point = (TextView)findViewById(R.id.points);
        NavigationView navigation = findViewById(R.id.navigationView);
        navigation.setNavigationItemSelectedListener(this);
        navigation.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        Navigation.setViewNavController(navigation,navController);
        EditText field1 = (EditText)findViewById(R.id.search);
        field1.setSelection(0);
        field1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Search newFragment = new Search();
                Tours newsecondFragment = new Tours();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(field1.length() >= 1){
                    addtown(field1);
                    transaction.replace(R.id.navHostFragment,newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else{
                    transaction.replace(R.id.navHostFragment,newsecondFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference();

        if (user.isAnonymous()) {
            point.setText("0");
        }else{
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long user1 = dataSnapshot.child("user").child(uid).child("points").getValue(Long.class);
                    String username1 = dataSnapshot.child("user").child(uid).child("points").getValue(String.class);
                    String user2 = user1.toString().trim();
                    username.setText(username1);
                    point.setText(user2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (user != null) {
            if (user.isAnonymous()) {
                username.setText("Anonymous");
            }
        }
        else{
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user1 = dataSnapshot.child("user").child(uid).child("username").getValue(String.class);
                    username.setText(user1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void addtown(EditText editext){
        field1 = editext.getText().toString();
    }

    public static String getField1() {
        return field1;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.tours:
                getSupportFragmentManager().beginTransaction().replace(R.id.tours, new Tours());
                break;
            case R.id.leaderboard:
                Toast.makeText(this,"Sorry but this function is come in next update",Toast.LENGTH_SHORT).show();
                break;
            case R.id.favourites:
                Toast.makeText(this,"Sorry but this function is come in next update",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tourcode:
                Toast.makeText(this,"Sorry but this function is come in next update",Toast.LENGTH_SHORT).show();
                break;
            case R.id.mypoints:
                Toast.makeText(this,"Sorry but this function is come in next update",Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:
                Toast.makeText(this,"Sorry but this function is come in next update",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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

    @Override
    protected void onStart() {
        super.onStart();
        hideNavigationBan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideNavigationBan();
    }
}
