package com.example.themoviebox;

import com.example.themoviebox.Model.Movie;
import com.example.themoviebox.Model.Trailer;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieDBApi {

    public static final String API_KEY = "2662a6a7ebcb864e1ca43389e4660dcf";
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static MovieService movieService = null;

    public static MovieService getMovieService() {

        if (movieService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            movieService = retrofit.create(MovieService.class);

        }
        return movieService;
    }

    public interface MovieService {

        @GET("popular?key=" + API_KEY)
        Call<Movie> getMovieDetails();

        @GET("{movie_id}/videos?key=" + API_KEY)
        Call<Trailer> getTrailerDetails(@Path("movie_id") int movie_id);

        @GET("popular")
        Call<Movie> getMoreMovieDetails(@Query("key") String API_KEY, @Query("page") int page_no);

//        @GET("{movie_id}/videos")
//        Call<Trailer> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);
    }
}
