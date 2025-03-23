package com.he172006.onlineclothesshop.adapter;




import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;




import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Review;


import java.util.ArrayList;
import java.util.List;




public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;




    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.txtReviewText.setText(review.reviewText);
        holder.ratingBar.setRating(review.rating);
    }
    public void updateReviews(List<Review> newReviews) {
        this.reviewList = new ArrayList<>(newReviews);
        notifyDataSetChanged();
    }




    @Override
    public int getItemCount() {
        return reviewList.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReviewText;
        RatingBar ratingBar;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReviewText = itemView.findViewById(R.id.txtReviewText);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
        }
    }
}






