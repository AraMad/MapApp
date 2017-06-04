package mapapp.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Arina on 12.04.2017
 */

public interface PrivatAPIInterface {
    @GET("infrastructure?json&atm&address=&")
    Call<String> getATMInfo(@Query("city") String city);
}
