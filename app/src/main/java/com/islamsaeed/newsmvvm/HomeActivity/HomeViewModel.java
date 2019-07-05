package com.islamsaeed.newsmvvm.HomeActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.islamsaeed.newsmvvm.API.APIManager;
import com.islamsaeed.newsmvvm.API.Model.ArticlesItem;
import com.islamsaeed.newsmvvm.API.Model.NewsResponce;
import com.islamsaeed.newsmvvm.API.Model.SourcesItem;
import com.islamsaeed.newsmvvm.API.Model.SourcesResponse;
import com.islamsaeed.newsmvvm.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    /** Now this object is empty , i will defined it when API answer me in Response  */
    public MutableLiveData<List<SourcesItem>> sources = new MutableLiveData<>();

    /**
     * To show Loading
     * then the activity will observe showLoading
     */
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();

    /** To put the list of articles item in MutableLiveData*/
    public MutableLiveData<List<ArticlesItem>> articles = new MutableLiveData<>();

    /**To show message if API became in onFailure  */
    public MutableLiveData<String> alertMessage = new MutableLiveData<>();




    public HomeViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * Here we will create the methods which will handle logic
     * First
     * Calling API
     */
    public void loadNewsSources() {

        /** To show Loading*/
        showLoading.setValue(true);
        APIManager.getApis()
                .getNewsSources(Constants.API_KEY, Constants.LANGUAGE)
                /*enqueue here doing background Thread
                 * and calling API
                 * and finally get back data been parsed with JSON  */
                .enqueue(new Callback<SourcesResponse>() {
                    @Override
                    public void onResponse(Call<SourcesResponse> call, Response<SourcesResponse> response) {

                        /** To hide Loading*/
                        showLoading.setValue(false);


                        /**
                         *Here i get Data which i will carry in a List of newsSources(That's will happened normally)
                         *But The new step here that i will Put my List of newsSources
                         *  in the LifeData , that means i will put the whole object which
                         *  been came from Response in the LifeData
                         *  i will go up there and declare an object from
                         *  MutableLifeData , not LifeData only
                         *
                         */

                        if (response.body() != null) {
                            // postValue will create a new thread to change data
                            // setValue  in the same thread
                            sources.setValue(response.body().getSources());
                        }
                    }

                    @Override
                    public void onFailure(Call<SourcesResponse> call, Throwable t) {

                        /** To hide Loading*/
                        showLoading.setValue(false);

                        /** To show message*/
                        alertMessage.setValue(t.getLocalizedMessage());

                    }
                });
    }


    /** Second
     * to Load news when i tabbed on item
     * i will call this method when user click on a tab*/

    public void loadNewsBySourceId (String sourceId){
        showLoading.setValue(true);
        APIManager.getApis()
                .getNews(Constants.API_KEY,Constants.LANGUAGE , sourceId)
                .enqueue(new Callback<NewsResponce>() {
                    @Override
                    public void onResponse(Call<NewsResponce> call, Response<NewsResponce> response) {
                        showLoading.setValue(false);
                        /** Here response will get List of Articles
                         * and i will put this list in a MutableLiveData
                         * and i will put the data in this MutableLiveData(articles)
                         * when i received a response*/
                        if (response.body()!=null)
                        articles.setValue(response.body().getArticles() );
                    }

                    @Override
                    public void onFailure(Call<NewsResponce> call, Throwable t) {
                        showLoading.setValue(false);

                        /** To show message*/
                        alertMessage.setValue(t.getLocalizedMessage());


                    }
                });


    }
}
