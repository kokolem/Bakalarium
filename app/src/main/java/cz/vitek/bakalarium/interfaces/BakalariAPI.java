package cz.vitek.bakalarium.interfaces;

import cz.vitek.bakalarium.pojos.HomeworkList;
import cz.vitek.bakalarium.pojos.LoginData;
import cz.vitek.bakalarium.pojos.TokenData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BakalariAPI {
    @GET("login.aspx?&pm=ukoly")
    Call<HomeworkList> getHomework(@Query("hx") String token);

    @Headers({"accept: text/json"})
    @GET("if/2/gethx/{username}")
    Call<TokenData> getTokenData(@Path("username") String username);

    @Headers({"accept: text/json"})
    @GET("if/2/login")
    Call<LoginData> getLoginData(@Header("Authorization") String auth);
}
