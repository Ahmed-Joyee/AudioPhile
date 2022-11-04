package app.android.audiophile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ChipNavigationBar chipNavigationBar;
    private DrawerLayout drawerLayout;
    private Fragment fragment = null;
    TextView nav_user_name, nav_name;
    Bundle bundle;
    Animation aniFade, aniFade2;

    DatabaseReference reference;
    Query checkUser;
    private String name, em, username, ph, pass, uid;
    private NavigationView navigationView;
    ActionBar actionBar;

    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bundle = getIntent().getExtras();
        fragment = new FeedFragment();
        fragment.setArguments(bundle);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        getSupportActionBar().hide();
        actionBar = getSupportActionBar();
        actionBar.hide();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FAEDF0"));
        actionBar.setBackgroundDrawable(colorDrawable);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        nav_user_name = (TextView) headerView.findViewById(R.id.nav_header_user_name);
        nav_name = (TextView) headerView.findViewById(R.id.nav_header_name);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//        checkUser = reference.orderByChild("id").equalTo(uid);

        aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        aniFade2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        appBarLayout = findViewById(R.id.appBarLayout);
        loadFragment(new FeedFragment());
        Menu menu = navigationView.getMenu();
        MenuItem item1 = menu.getItem(3);
        View menuIcon = findViewById(R.id.menu_icon);
        item1.setChecked(true);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void loadFragment(Fragment fragment) {
        if(getCurrentFragment() != fragment){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            if(getCurrentFragment()!=null)
            {
                fm.beginTransaction().remove(getCurrentFragment()).commit();
            }

            ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.frag_container, fragment);
            //ft.commit();
            ft.commitNow();
        }
    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

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
                    Fragment fragment;
                    switch (id) {
                        case R.id.feed:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            navigationView.setCheckedItem(R.id.feed);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            fragment = new FeedFragment();
                            fragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
                            break;

                        case R.id.explore:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            navigationView.setCheckedItem(R.id.explore);
                            fragment = new ExploreFragment();
                            fragment.setArguments(bundle);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
                            break;

                        case R.id.local:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            fragment = new LocalSongsFragment();
                            fragment.setArguments(bundle);
                            navigationView.setCheckedItem(R.id.local);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
                            break;

//                        case R.id.myplaylists:
//                            navigationView.setCheckedItem(R.id.myplaylists);
//                            startActivity(new Intent(MainActivity.this, myplaylists.class));
//                            break;

                        case R.id.nav_userlist:
                            actionBar.show();
                            appBarLayout.setVisibility(View.GONE);
                            fragment = new SearchFriendsFragment();
                            fragment.setArguments(bundle);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.nav_userlist);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
                            break;

                        case R.id.nav_friends:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            fragment = new FriendFragment();
                            fragment.setArguments(bundle);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.nav_userlist);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
                            break;

                        case R.id.contact_btn:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            fragment = new ContactUsFragment();
                            fragment.setArguments(bundle);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.contact_btn);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
                            break;


                        case R.id.nav_chat:
                            actionBar.show();
                            appBarLayout.setVisibility(View.GONE);
                            fragment = new ChatFragment();
                            fragment.setArguments(bundle);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.nav_chat);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                   fragment).addToBackStack(null).commit();
                            break;


                        case R.id.nav_user_profile:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.nav_user_profile);
                            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
//                            finish();
                            break;

                        case R.id.sign_out:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.sign_out);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            break;

                        case R.id.meet_dev:
                            actionBar.hide();
                            appBarLayout.setVisibility(View.VISIBLE);
                            fragment = new MeetDevFragment();
                            fragment.setArguments(bundle);
                            if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                            navigationView.setCheckedItem(R.id.meet_dev);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                                    fragment).addToBackStack(null).commit();
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
            super.onBackPressed();

        moveTaskToBack(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}