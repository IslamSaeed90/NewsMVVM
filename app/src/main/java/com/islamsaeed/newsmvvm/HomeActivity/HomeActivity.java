package com.islamsaeed.newsmvvm.HomeActivity;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.islamsaeed.newsmvvm.API.Model.ArticlesItem;
import com.islamsaeed.newsmvvm.API.Model.SourcesItem;
import com.islamsaeed.newsmvvm.Base.BaseActivity;
import com.islamsaeed.newsmvvm.NewsAdapter;
import com.islamsaeed.newsmvvm.R;

import java.util.List;

public class HomeActivity extends BaseActivity {

    TabLayout tabLayout;
    RecyclerView recyclerView;
    List<ArticlesItem> articles ;

    NewsAdapter newsAdapter ;

    /**First
     * i will create an object from HomeViewModel
     * then i will initialize it in onCreate*/
    HomeViewModel homeViewModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** here i attach the ViewModel with this activity or fragment ( with LifeCycle)*/
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        initView();
        initRecyclerView();

        /** Second
         *here i will call newsSources (The API that get sources)
         * API will get Data and this data will been putted in mutableLifeData
         * then observable pattern will told me that the data been arrived
         * */
        homeViewModel.loadNewsSources();
        /** when API been called , i want to show loading
         * what should i do ??
         * i will do Listener in ViewModel
         * we will create LiveData  and it contains a boolean , when it true : show progressbar
         * false : hide progressbar  , let's go to ViewModel class*/

        /** Fourth */
        subscribeToLiveData();

    }

    /** Third
     * create a method that subscribedLiveData
     * **/
    public void subscribeToLiveData (){
        /**
         * Here i will observe the data which in mutableLiveData */
        homeViewModel.sources.observe(this, new Observer<List<SourcesItem>>() {
            @Override
            public void onChanged(List<SourcesItem> sourcesItems) {
                /** THis Method been called when data been changed
                 * then when newsSources been changed i will put it in a tab */
                setTabLayoutWithNewsSources(sourcesItems);
            }
        });

        /** Fifth
         * observe showLoading*/
        homeViewModel.showLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean show) {
             if (show)
             {
                 showProgressBar(R.string.loading);
             }else hideProgressDialog();

            }
        });


        /** seventh
         *  When the data been changed on ViewModel , i must do subscribe*/
            homeViewModel.articles.observe(this, new Observer<List<ArticlesItem>>() {
                @Override
                public void onChanged(List<ArticlesItem> articlesItems) {
                 /** i will go to NewsAdapter to  set ChangeData  */
                 newsAdapter.changeData(articlesItems);
                }
            });

            /** Eighth
             * to observe message*/
            homeViewModel.alertMessage.observe(this, new Observer<String>() {
                @Override
                public void onChanged(String message) {
                    showMessage(null , message , getString(R.string.ok));

                }
            });

    }

    public void initView(){
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);


    }
    public void initRecyclerView (){
        newsAdapter = new NewsAdapter(null);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

    }

    private void setTabLayoutWithNewsSources(final List<SourcesItem> sources) {
        // معايا list المفروض اني احطها فال tab
        for (int i =0 ; i<sources.size();i++)
        {
            // هاخد اسم كل source وابعته لل tab
            TabLayout.Tab tab = tabLayout.newTab(); //   ولازم اعمل الخطوة دي الأول عشان اشتغل مع ال tabLayout , بقول لل tabLayout  يعملي تاب جديدة
            tab.setText(sources.get(i).getName());
            tab.setTag(sources.get(i));
            /*هاتفيدني اما اعوز اجيب parameters من ال tab  ده
             * يبقي كل tab  محطوط معاه ال tag  بتاعه وال object  بتاع ال source*/
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // انا عايز هنا انادي عال API

                /*هنا عشان اجيب ال id هاجيب الأول ال tab position
                 * ومكانه ده هو هو مكانه اللي فال List  اللي جاية فال Parameter
                 * فلازم اروح اجيب ال id  من الليست دي*/

                // الحل الأول
//                int tabPos = tab.getPosition();
//                loadNewsBySourceId(sources.get(tabPos).getId());

                // الحل الثاني تكملة اللي فوق بتاع ال tag

                SourcesItem item = ((SourcesItem) tab.getTag());
                /**
                 * sixth
                 * here i will call loadNewsBySourcesId when user click on a tab*/
              homeViewModel.loadNewsBySourceId(item.getId());
              /** then i will go to subscribeToLiveData metho to set that change*/
                // هاروح اعمل ال adapter
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                /** to make first tab active with me in the first time */
                onTabSelected(tab);
            }
        });
        /** to make first tab active with me in the first time */
        tabLayout.getTabAt(0).select();
    }


}
