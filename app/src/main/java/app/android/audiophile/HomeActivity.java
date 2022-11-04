package app.android.audiophile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private DrawerLayout drawerLayout;
    private Fragment fragment = null;
    TextView nav_user_name, nav_name;

    Animation aniFade, aniFade2;

    DatabaseReference reference;
    Query checkUser;
    private String name, em, username, ph, pass, uid;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        fragment = new FeedFragment();
        fragment.setArguments(bundle);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().hide();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();


        chipNavigationBar = findViewById(R.id.chipNavigation);
        chipNavigationBar.setItemSelected(R.id.feed, true);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        nav_user_name = (TextView) headerView.findViewById(R.id.nav_header_user_name);
        nav_name = (TextView) headerView.findViewById(R.id.nav_header_name);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");
        checkUser = reference.orderByChild("id").equalTo(uid);

        aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        aniFade2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);


        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i){
                case R.id.chat:
                    fragment = new ChatFragment();
                    if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                    break;
                case R.id.feed:
                    fragment = new FeedFragment();
                    if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                    break;
//                case R.id.explore:
//                    fragment = new ExploreFragment();
//                    if(!MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().reset();
//                    break;
                case R.id.friend:
                    fragment = new SearchFriendsFragment();
                    if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                    break;
            }

            if(fragment != null){
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    username = snapshot.child(uid).child("username").getValue(String.class);
                    name = snapshot.child(uid).child("name").getValue(String.class);
                    em = snapshot.child(uid).child("email").getValue(String.class);
                    ph = snapshot.child(uid).child("phone").getValue(String.class);
                    pass = snapshot.child(uid).child("password").getValue(String.class);
                    nav_user_name.setText(username );
                    nav_name.setText(name);
                    Log.i(TAG, "onDataChange: " + username);
                }
                else{
                    nav_user_name.setText("Anonymous");
                    nav_name.setText("name" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                nav_user_name.setText("Anonymous");
                nav_name.setText("name" );
            }
        });

        this.getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Fragment current = getCurrentFragment();
                        if (current instanceof ContactUsFragment) {
                            navigationView.setCheckedItem(R.id.contact_btn);
                        } else {
                            navigationView.setCheckedItem(R.id.meet_dev);
                        }
                    }
                });

        navigationDrawer();
    }

    public Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.frag_container);
    }

    private void navigationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
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
                    if(item.hasSubMenu())
                    {
                        Log.i(TAG, "onNavigationItemSelected: haass " + item);
                        Menu menu2 = item.getSubMenu();
                        for (int ij = 0; ij < menu2.size(); ij++) {
                            MenuItem item2 = menu2.getItem(ij);
                            Log.i(TAG, "onNavigationItemSelected: jgifs " + item2);
                            if (item2.isChecked()) {
                                it = item2.getItemId();
                                item2.setChecked(false);
                                Log.i(TAG, "onNavigationItemSelected: selft " + it);
                            }
                        }
                    }
                    else
                    {
                        if (item.isChecked()) {
                            Log.i(TAG, "onNavigationItemSelected: nopes " + item);
                            it = item.getItemId();
                            item.setChecked(false);
                        }
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
                        case R.id.local:
                            navigationView.setCheckedItem(R.id.local);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    new LocalSongsFragment()).addToBackStack(null).commit();
                            break;

//                        case R.id.myplaylists:
//                            navigationView.setCheckedItem(R.id.myplaylists);
//                            startActivity(new Intent(MainActivity.this, myplaylists.class));
//                            break;

                        case R.id.nav_userlist:
                            navigationView.setCheckedItem(R.id.nav_userlist);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    new SearchFriendsFragment()).addToBackStack(null).commit();
                            break;

//                        case R.id.nav_friends:
//                            navigationView.setCheckedItem(R.id.nav_friends);
//                            loadFragment(new FriendsFragment());
//                            break;

                        case R.id.contact_btn:
                            navigationView.setCheckedItem(R.id.contact_btn);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    new ContactUsFragment()).addToBackStack(null).commit();
                            break;


                        case R.id.nav_chat:
                            navigationView.setCheckedItem(R.id.nav_chat);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    new ChatFragment()).addToBackStack(null).commit();
                            break;


                        case R.id.nav_user_profile:
                            navigationView.setCheckedItem(R.id.nav_user_profile);
                            startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
                            finish();
                            break;

                        case R.id.sign_out:
                            navigationView.setCheckedItem(R.id.sign_out);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            break;

                        case R.id.meet_dev:
                            navigationView.setCheckedItem(R.id.meet_dev);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    new MeetDevFragment()).addToBackStack(null).commit();
                            break;

                        default:
                            break;
                    }
                    menuItem.setChecked(true);
                    //setTitle(menuItem.getTitle());
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
            super.onBackPressed();

        moveTaskToBack(true);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}