package com.mygamelogic.myplayer.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mygamelogic.myplayer.Adapter.FragmentPageAdapter;
import com.mygamelogic.myplayer.CustomTools.MyTools;
import com.mygamelogic.myplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 12/07/18.
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher,TextView.OnEditorActionListener{
    private EditText editext_search;
    private TabLayout tab_layout;
    private List<String> searchStrArray = new ArrayList<>();
    private Boolean doubleBackToExitPressedOnce = false;
    private ViewPager viewpager;
    private FragmentPageAdapter fragmentPageAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        setClickListener();
        setStartAnimation();
        setSearchBarPosition();
    }

    private void initViews() {
        editext_search = (EditText) findViewById(R.id.editext_search);
        viewpager=(ViewPager) findViewById(R.id.viewpager);
        tab_layout=(TabLayout) findViewById(R.id.tab_layout);
    }

    private void setClickListener() {
        editext_search.setOnClickListener(this);
        findViewById(R.id.imageview_gosearch).setOnClickListener(this);
        findViewById(R.id.imageview_favourite).setOnClickListener(this);
        editext_search.addTextChangedListener(this);
        editext_search.setOnEditorActionListener(this);
    }

    private void setStartAnimation() {
        //show logo with with animation
        MyTools.scaleAndAlphaAnimation(findViewById(R.id.layout_logo));
        //show searchbar after some times
        findViewById(R.id.layout_search).setVisibility(View.VISIBLE);
        MyTools.alphaAnim(findViewById(R.id.layout_maintobbar));
    }

    //set searchbar initial position and animate after 2 sec
    private void setSearchBarPosition() {
        LinearLayout layout_maintobbar = (LinearLayout) findViewById(R.id.layout_maintobbar);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrHeight = metrics.heightPixels;
        layout_maintobbar.setY(scrHeight / 2);
    }

    //hide logo and other thing at same time.
    //when click on editetext for first time
    private void showSearchBardToTop() {
        MyTools.hideAndScaleAnimation(findViewById(R.id.layout_logo));
        setSearchBarToTopAnim();
        findViewById(R.id.textview_listenlabel).setVisibility(View.GONE);
        findViewById(R.id.topbar).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_listviewmain).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_main).setBackgroundColor(MyTools.getColorBack(this,R.color.white_light));
    }

    private void setSearchBarToTopAnim() {
        LinearLayout layout_maintobbar = (LinearLayout) findViewById(R.id.layout_maintobbar);
        layout_maintobbar.animate().translationY(0).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
    }


    //---------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finish();
            System.exit(0);
            return;
        }
        doubleBackToExitPressedOnce = true;
        MyTools.showToast(this, getString(R.string.backbtn_text));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    //--------------------------------------------------------------------------------------------------------
//View Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editext_search:
                if (v.getId() == R.id.editext_search) {
                    if (findViewById(R.id.layout_maintobbar).getY() > 100) {
                        showSearchBardToTop();
                    }
                }
                break;
            case R.id.imageview_gosearch:
                searchButtonTapped();
                break;
            case R.id.imageview_favourite:
                Intent intent = new Intent(this, FavouriteActivity.class);
                startActivity(intent);
                break;
        }
    }

//on Keyboard go button pressed
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            searchButtonTapped();
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    //on editetext text changed
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //if search text is not empty show go button
        if (editext_search.getText().toString().isEmpty()) {
            findViewById(R.id.imageview_gosearch).setVisibility(View.GONE);
        } else {
            findViewById(R.id.imageview_gosearch).setVisibility(View.VISIBLE);
        }
    }
//---------------------------------------------------------------------------------------------------
//methode called on search button called
    private void searchButtonTapped() {
        if ((!editext_search.getText().toString().isEmpty())) {
            handleSearchArr(editext_search.getText().toString());
            findViewById(R.id.imageview_gosearch).setVisibility(View.GONE);
            hideKeyboard(editext_search);
            setViewPager(editext_search.getText().toString());
        } else {
            MyTools.showToast(this, getString(R.string.nosearch_text));
        }
    }

    private void setViewPager(String searchText){
        if(fragmentPageAdapter==null){
            fragmentPageAdapter=new FragmentPageAdapter(getSupportFragmentManager(),searchStrArray);
            viewpager.setAdapter(fragmentPageAdapter);
            tab_layout.setupWithViewPager(viewpager, true);
            viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    setSearchText(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        }else {
            fragmentPageAdapter.setDataLoaderNotifier(searchStrArray);
        }
        setCurrentTab();
    }
    private void setCurrentTab(){
        if(fragmentPageAdapter!=null){
            viewpager.setCurrentItem(searchStrArray.size());
        }
    }
//set ediietxt text to search text on tab chnaged
    private void setSearchText(int position){
        if(searchStrArray.size()>position){
            editext_search.setText(searchStrArray.get(position));
            editext_search.setSelection(searchStrArray.get(position).length());
        }
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {}
    }

//--------------------------------------------------------------------------------------------------------------------------
    //add search text to array to handle back and forth of search
    private void handleSearchArr(String searchStr) {
        if (searchStrArray.size() == 4) {
            //if reaches max value remove first one
            searchStrArray.remove(0);
            searchStrArray.add(searchStr);
        } else {
            searchStrArray.add(searchStr);
        }
    }

}
