package mapapp.singletons;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static mapapp.settings.Constants.BASE_URL;

/**
 * Created by Arina on 05.06.2017
 */

public class RetrofitClient {

    private static Retrofit client;

    public static synchronized Retrofit getInstance(){
        if (client == null){
            client = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return client;
    }
}
