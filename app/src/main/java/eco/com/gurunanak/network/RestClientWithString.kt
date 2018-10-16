package eco.com.gurunanak.network


import com.tudle.network.api_services
import eco.com.gurunanak.utlity.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RestClientWithString {


    companion object {







        fun create(str:String): api_services {


            var interceptor =  HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            var client =  OkHttpClient.Builder().addInterceptor(interceptor).build();

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory
                                    .create())
                    .baseUrl(Constant.BASE_URL+str).client(client)
                    .build()


            return retrofit.create(api_services::class.java)
        }
    }
}