package app.android.audiophile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView nav_user_name, nav_name;

    FirebaseAuth mAuth;
    DatabaseReference reference;
    Query checkUser;

    String name, em, username, ph, pass, uid;

    static final float END_SCALE = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        drawerLayout = findViewById(R.id.prof_drawer);
        navigationView = findViewById(R.id.prof_navigation_view);

        View headerView = navigationView.getHeaderView(0);
        nav_user_name = (TextView) headerView.findViewById(R.id.nav_header_user_name);
        nav_name = (TextView) headerView.findViewById(R.id.nav_header_name);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
//        checkUser = reference.orderByChild("id").equalTo(uid);

        loadFragment(new SettingsFragment());
        Menu menu = navigationView.getMenu();
        MenuItem item1 = menu.getItem(1);
        item1.setChecked(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    username = user.getUsername();
                    em = user.getEmail();
                    ph = user.getMobile();
                    pass = user.getPassword();
                    username.toUpperCase();
                    em.toLowerCase();
                    nav_user_name.setText(username );
                    nav_name.setText(em);
                    Log.i(TAG, "onDataChange: " + username);
                }
                else{
                    nav_user_name.setText("Anonymous");
                    nav_name.setText("name" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        naviagtionDrawer();
    }

    public Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.frag_container);
    }

    private void naviagtionDrawer() {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setCheckedItem(R.id.nav_home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int it = -1;
                Menu menu = navigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (item.isChecked()) {
                        Log.i(TAG, "onNavigationItemSelected: nopes " + item);
                        it = item.getItemId();
                        item.setChecked(false);
                    }
                }
                menuItem.setChecked(false);
                int id = menuItem.getItemId();

                Log.i(TAG, "onNavigationItemSelected: " + it + " " + menuItem);

                if(it==id)
                {
                    menuItem.setChecked(true);
                    return false;
                }
                else
                {
                    switch (id) {

                        case R.id.change_pass:
                            navigationView.setCheckedItem(R.id.change_pass);
                            loadFragment(new UpdatePasswordFragment());
                            break;


                        case R.id.settings_nav:
                            navigationView.setCheckedItem(R.id.settings_nav);
                            loadFragment(new SettingsFragment());
                            break;

                        case R.id.nav_mail:
                            navigationView.setCheckedItem(R.id.nav_mail);
                            loadFragment(new UpdateEmailFragment());

                            break;

                        case R.id.nav_user_home:
                            navigationView.setCheckedItem(R.id.nav_user_home);
                            startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
                            break;

                        default:
                            break;
                    }
                    menuItem.setChecked(true);
                    setTitle(menuItem.getTitle());
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            }
        });
    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }

        moveTaskToBack(true);
    }
    private void loadFragment(Fragment fragment) {
        if(getCurrentFragment() != fragment){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            if(getCurrentFragment()!=null)
            {
                fm.beginTransaction().remove(getCurrentFragment()).commit();
            }

            ft.replace(R.id.prof_container, fragment);
            //ft.commit();
            ft.commitNow();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}