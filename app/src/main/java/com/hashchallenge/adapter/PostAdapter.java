package com.hashchallenge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.hashchallenge.R;
import com.hashchallenge.activity.MainActivity;
import com.hashchallenge.model.Comment;
import com.hashchallenge.model.Post;

import java.util.List;

public class PostAdapter extends AbstractExpandableItemAdapter<PostAdapter.PostViewHolder, PostAdapter.CommentViewHolder> {

    Context context;
    List<Post> posts;

    public PostAdapter(List<Post> posts, Context context){
        setHasStableIds(true);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getGroupCount() {
        return posts.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return posts.get(groupPosition).getComments().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return posts.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return posts.get(groupPosition).getComments().get(childPosition).getId();
    }

    @Override
    @NonNull
    public PostViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    @NonNull
    public CommentViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull PostViewHolder holder, int groupPosition, int viewType) {
        Post post = posts.get(groupPosition);

        holder.imageView.setImageResource(R.drawable.ic_person_black_24dp);
        holder.username.setText(post.getUser().getName());
        holder.postTitle.setText(post.getTitle());
        holder.postBody.setText(post.getBody());

        // Set margin = 0 no primeiro post
        if(groupPosition == 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cv.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            holder.cv.requestLayout();
        }

        // Ao clicar no post para exibir/esconder os comments, chama o método para buscar os comments e altera o texto
        holder.cv.setOnClickListener(view -> {

            holder.showComments = !holder.showComments;

            if (context instanceof MainActivity && holder.showComments) {
                ((MainActivity) context).getHashComments(post);
            }

            holder.clickComments.setText(holder.showComments ? R.string.comment_click_hide : R.string.comment_click_show);
        });
    }

    @Override
    public void onBindChildViewHolder(@NonNull CommentViewHolder holder, int groupPosition, int childPosition, int viewType) {
        Comment comment = posts.get(groupPosition).getComments().get(childPosition);

        holder.email.setText(comment.getEmail());
        holder.name.setText(comment.getName());
        holder.commentBody.setText(comment.getBody());

        // Só exibe o TextView COMMENTS no primeiro comment do post
        holder.comments.setVisibility(childPosition == 0 ? View.VISIBLE : View.GONE);

        // Não exibe o separator no ultimo comment
        holder.separator.setVisibility(childPosition != posts.get(groupPosition).getComments().size() - 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(@NonNull PostViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    public static class PostViewHolder extends AbstractExpandableItemViewHolder {

        CardView cv;
        ImageView imageView;
        TextView username;
        TextView postTitle;
        TextView postBody;
        TextView clickComments;
        boolean showComments;


        PostViewHolder(View itemView) {

            super(itemView);

            cv = itemView.findViewById(R.id.card_view_post);
            imageView = itemView.findViewById(R.id.image_view);
            username = itemView.findViewById(R.id.text_view_username);
            postTitle = itemView.findViewById(R.id.text_view_post_title);
            postBody = itemView.findViewById(R.id.text_view_post_body);
            clickComments = itemView.findViewById(R.id.text_view_click_comments);
            showComments = false;
        }
    }

    public static class CommentViewHolder extends AbstractExpandableItemViewHolder {

        TextView comments;
        TextView email;
        TextView name;
        TextView commentBody;
        View separator;

        CommentViewHolder(View itemView) {

            super(itemView);

            comments = itemView.findViewById(R.id.text_view_comments);
            email = itemView.findViewById(R.id.text_view_comment_email);
            name = itemView.findViewById(R.id.text_view_comment_name);
            commentBody = itemView.findViewById(R.id.text_view_comment_body);
            separator = itemView.findViewById(R.id.separator_comment_item);
        }
    }

}
