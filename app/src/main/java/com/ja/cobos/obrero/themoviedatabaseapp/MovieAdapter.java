package com.ja.cobos.obrero.themoviedatabaseapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Juan Antonio Cobos Obrero on 22/01/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mCtx;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context ctx, ArrayList<Movie> movies)  {
        mCtx = ctx;
        mMovies = movies;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(item);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);

        Picasso.with(mCtx).load(movie.getImagePath())
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .into(holder.mMovieImage);

        holder.mMovieTitle.setText(movie.getTitle());
        holder.mMovieYear.setText(movie.getYear());
        holder.mMovieOverview.setText(movie.getOverview());

    }

    @Override
    public int getItemCount() {
        if(mMovies != null) {
            return mMovies.size();
        } else {
            return 0;
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView mMovieImage;
        private TextView mMovieTitle, mMovieYear, mMovieOverview;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mMovieImage = (ImageView) itemView.findViewById(R.id.movieImage);
            mMovieTitle = (TextView) itemView.findViewById(R.id.movieTitle);
            mMovieYear = (TextView) itemView.findViewById(R.id.movieYear);
            mMovieOverview = (TextView) itemView.findViewById(R.id.movieOverview);
        }
    }
}
