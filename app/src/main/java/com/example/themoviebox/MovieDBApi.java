package com.example.themoviebox;

import com.example.themoviebox.Model.Movie;
import com.example.themoviebox.Model.Trailer;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MovieDBApi {

    public static final String API_KEY = "2662a6a7ebcb864e1ca43389e4660dcf";
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }

    public interface MovieService {

        @GET("popular?key=" + API_KEY)
        Call<Movie> getMovieDetails();

        @GET("{movie_id}/videos?key=" + API_KEY)
        Call<Trailer> getTrailerDetails(@Path("movie_id") int movie_id);

//        @GET("{movie_id}/videos")
//        Call<Trailer> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);
    }
}
