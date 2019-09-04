package com.hashchallenge.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.hashchallenge.R;
import com.hashchallenge.adapter.PostAdapter;
import com.hashchallenge.model.Comment;
import com.hashchallenge.model.HashResponse;
import com.hashchallenge.model.Post;
import com.hashchallenge.model.User;
import com.hashchallenge.service.HashBuilder;
import com.hashchallenge.service.HashService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Context context;

    HashService service = HashBuilder.createService(HashService.class);
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    List<User> users = new ArrayList<>();
    List<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        recyclerView = findViewById(R.id.recycler_view);

        getHashPosts();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    /**
     * Faz o parse dos dados para serem exibidos em tela
     */
    private void parseData() {

        // Seta o usuario de em seus respectivos posts
        for (Post post : posts) {

            post.setUser(users
                    .stream()
                    .filter(u -> u.getId() == post.getUserId())
                    .findFirst()
                    .orElse(null));
        }

        setupAdapter();
    }

    /**
     * Configura o adapter da AdvancedRecyclerView
     */
    private void setupAdapter() {

        // Setup expandable feature and RecyclerView
        RecyclerViewExpandableItemManager expMgr = new RecyclerViewExpandableItemManager(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        postAdapter = new PostAdapter(posts, context);
        recyclerView.setAdapter(expMgr.createWrappedAdapter(postAdapter));

        // NOTE: need to disable change animations to ripple effect work properly
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        expMgr.attachRecyclerView(recyclerView);
    }

    /**
     * Método que realiza os requests REST dos dados desejados
     */
    private void getHashPosts() {

        // Monta os observables dos requests
        Observable<List<User>> userObservable = service
                .getUsers("id", "asc")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<List<Post>> postObservable = service
                .getPosts("id", "asc")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        // Combina os 2 requests e retorna um observable HashResponse que contem os 2 responses
        Observable<HashResponse> combined = Observable.zip(userObservable, postObservable, (users, posts) -> new HashResponse(users, posts));

        // Desmembra o responde nos 2 objetos desejados e chama o metodo parseData()
        compositeDisposable.add(
            combined.subscribe(response -> {
                users = response.getUsers();
                posts = response.getPosts();

                parseData();
            })
        );

    }

    /**
     * Método que busca os comments de cada post separadamente
     *
     * @param post
     */
    public void getHashComments(Post post) {

        Call<List<Comment>> call = service.getComments(post.getId(), "id", "asc");

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                if(!response.isSuccessful()) {
                    Log.e("MainActivity", "Erro code: " + response.code());
                    return;
                }

                // Seta os comments dentro do post e notifica o adapter para atualizar a lista
                post.setComments(response.body());
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("MainActivity", "Erro ao buscar comments: " + t.getMessage());
            }
        });
    }

}
