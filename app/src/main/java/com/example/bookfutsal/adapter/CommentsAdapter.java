package com.example.bookfutsal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfutsal.R;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<String> commentList;

    public CommentsAdapter(List<String> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_items, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String comment = commentList.get(position);
        // decouper le commentairer de facon a avoir le nom et le cmmentaire séparé
        String[] parts = comment.split(":");

        String username = parts[0].trim(); // enlever les espaces avant/après le nom d'utilisateur
        String message = parts[1].trim(); // enlever les espaces avant/après le commentaire

        if (parts.length > 2) {
            for (int i = 2; i < parts.length; i++) {
                message += ":" + parts[i].trim(); // ajouter le reste du message en conservant les ":" entre chaque partie
            }
        }
        holder.bind(username, message);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView commentTextViewName, textViewComment;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextViewName = itemView.findViewById(R.id.item_comment_name);
            textViewComment = itemView.findViewById(R.id.comment_value);
        }

        public void bind(String name, String comment) {
            textViewComment.setText(comment);
            commentTextViewName.setText(name);
        }
    }
}
