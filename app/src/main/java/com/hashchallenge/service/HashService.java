package com.hashchallenge.service;

import com.hashchallenge.model.Comment;
import com.hashchallenge.model.Post;
import com.hashchallenge.model.User;
import java.util.List;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface que contém a assinatura dos métodos da API REST
 */
public interface HashService {

    @GET("users")
    Observable<List<User>> getUsers(@Query("_sort") String sort, @Query("_order") String order);

    @GET("posts")
    Observable<List<Post>> getPosts(@Query("_sort") String sort, @Query("_order") String order);

    @GET("comments")
    Call<List<Comment>> getComments(@Query("postId") int postId, @Query("_sort") String sort, @Query("_order") String order);
}
