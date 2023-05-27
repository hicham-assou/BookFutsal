package com.example.bookfutsal.adapter;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfutsal.R;
import com.example.bookfutsal.activities.SportCenterDetail;
import com.example.bookfutsal.models.SportCenter;
import com.example.bookfutsal.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.SpreadBuilder;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    SportCenter center;
    private List<String> commentList;
    private User user;

    public CommentsAdapter(SportCenter center, List<String> commentList, User user) {
        this.center = center;
        this.commentList = commentList;
        this.user = user;
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
        // decouper le commentairer de facon a avoir le nom , date et le cmmentaire séparé
        String[] parts = comment.split(":");

        String username = parts[0].trim();
        String message = "";
        String date_hour = ""; //jack : 10/05/2023 16:27:26 : c'est cool

        for (int i = 1; i < 4; i++) {
            date_hour += parts[i].trim() + ":";
        }
        date_hour = date_hour.substring(0, date_hour.length() - 1); // retirer le dernier ":"


        if (parts.length > 4) {
            for (int i = 4; i < parts.length; i++) {
                message += parts[i].trim();
            }
        }

        holder.bind(username , message, date_hour, user);

        holder.delete_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete this comment ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String comment = commentList.get(position);
                            commentList.remove(position);
                            notifyItemRemoved(position);

                            // Supprimer le commentaire de la db
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = db.collection("centers").document(center.getNameCenter().toLowerCase());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("comments", FieldValue.arrayRemove(comment));
                            documentReference.update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void vv) {
                                            Toast.makeText(v.getContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                                            // Rafraîchir l'activité
                                            SportCenterDetail.refresh();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView commentTextViewName, textViewComment, commentTimeTextView;
        private ImageView delete_comment;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextViewName = itemView.findViewById(R.id.item_comment_name);
            textViewComment = itemView.findViewById(R.id.comment_value);
            commentTimeTextView = itemView.findViewById(R.id.comment_time);
            delete_comment = itemView.findViewById(R.id.delete_comment);

        }

        public void bind(String name, String comment, String time, User user) {
            textViewComment.setText(comment);
            commentTextViewName.setText(name);
            commentTimeTextView.setText(time);

            if (user != null){
                if (name.equals(user.getUsername())){
                    delete_comment.setVisibility(View.VISIBLE);
                    System.out.println("name " + name + " user " + user.getUsername());
                }

            }


        }
    }
}
