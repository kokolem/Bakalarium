package cz.vitek.bakalarium.Interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import cz.vitek.bakalarium.POJOs.HomeworkList;
import cz.vitek.bakalarium.POJOs.LoginData;
import cz.vitek.bakalarium.POJOs.TokenData;

public interface BakalariAPI {
    @GET("/bakaweb/login.aspx?&pm=ukoly")
    Call<HomeworkList> getHomework(@Query("hx") String token);

    @Headers({"accept: text/json"})
    @GET("/bakaweb/if/2/gethx/{username}")
    Call<TokenData> getTokenData(@Path("username") String username);

    @Headers({"accept: text/json"})
    @GET("/bakaweb/if/2/login")
    Call<LoginData> getLoginData(@Header("Authorization") String auth);
}
