package com.example.second_book_exchange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.second_book_exchange.fragment.CartFragment;
import com.example.second_book_exchange.fragment.HeartFragment;
import com.example.second_book_exchange.fragment.HomeFragment;
import com.example.second_book_exchange.fragment.MemberFragment;
import com.example.second_book_exchange.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ImageView ivHome, ivSearch, ivHeart, ivCart, ivMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon(R.drawable.home_click,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user);

                //把fragment裝進container裡, 並且顯示出來
                //針對homeFragment的點擊事件的接口, 換至MemberFragment
                HomeFragment homeFragment = HomeFragment.newInstance();
                changePage(homeFragment);
                homeFragment.setOnChangeTabListener(new HomeFragment.OnChangeTabListener() {
                    @Override
                    public void onChangeTabIcon() {
                        changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user_click);
                        changePage(MemberFragment.newInstance());
                    }
                });
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon(R.drawable.home,R.drawable.loupe_click,R.drawable.heart,R.drawable.cart,R.drawable.user);
                changePage(SearchFragment.newInstance());
            }
        });

        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart_click,R.drawable.cart,R.drawable.user);

                HeartFragment heartFragment = HeartFragment.newInstance();
                changePage(heartFragment);
                heartFragment.setOnHeartListener(new HeartFragment.OnChangeHeartListener() {
                    @Override
                    public void onChangeHeartIcon() {
                        changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user_click);
                        changePage(MemberFragment.newInstance());
                    }
                });
            }
        });

        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart,R.drawable.cart_click,R.drawable.user);

                CartFragment cartFragment = CartFragment.newInstance();
                changePage(cartFragment);
                cartFragment.setOnChangeCartListener(new CartFragment.OnChangeCartListener() {
                    @Override
                    public void onChangeCartIcon() {
                        changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user_click);
                        changePage(MemberFragment.newInstance());
                    }
                });
            }
        });

        ivMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user_click);
                changePage(MemberFragment.newInstance());
            }
        });

    }

    private void changePage(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                            //replace --> 設定不能返回,如果按上一頁的話,會直接離開app,所以換頁之後,上一個fragment就會被銷毀
        fragmentTransaction.replace(R.id.container,fragment).commit();
    }

    private void changeIcon(int homeIcon, int searchIcon, int heartIcon, int cartIcon, int memberIcon) {
        ivHome.setImageResource(homeIcon);
        ivSearch.setImageResource(searchIcon);
        ivHeart.setImageResource(heartIcon);
        ivCart.setImageResource(cartIcon);
        ivMember.setImageResource(memberIcon);
    }

    private void initView() {

        ivHome = findViewById(R.id.home);
        ivSearch = findViewById(R.id.search);
        ivHeart = findViewById(R.id.heart);
        ivCart = findViewById(R.id.cart);
        ivMember = findViewById(R.id.member);

        //一開始就是點選到首頁,所以首頁icon設效果
        changeIcon(R.drawable.home_click,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user);

        //把fragment裝進container裡, 並且顯示出來
        //針對homeFragment的點擊事件的接口, 換至MemberFragment
        HomeFragment homeFragment = HomeFragment.newInstance();
        changePage(homeFragment);
        homeFragment.setOnChangeTabListener(new HomeFragment.OnChangeTabListener() {
            @Override
            public void onChangeTabIcon() {
                changeIcon(R.drawable.home,R.drawable.loupe,R.drawable.heart,R.drawable.cart,R.drawable.user_click);
                changePage(MemberFragment.newInstance());
            }
        });

    }
}