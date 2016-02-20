package com.test.test;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.test.test.models.Location;
import com.test.test.models.Photo;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PhotoActivity extends AppCompatActivity {
    @Bind(R.id.photos_recycyler_view) RecyclerView mPhotosRecyclerView;

    private PhotosRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        ButterKnife.bind(this);

        mAdapter = new PhotosRecyclerAdapter(new ArrayList<Location>(), this);
        mPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPhotosRecyclerView.setAdapter(mAdapter);

        Location.getQuery()
                .whereEqualTo("photographer", ParseUser.getCurrentUser())
                .include("photo")
                .findInBackground(new FindCallback<Location>() {
                    @Override
                    public void done(List<Location> list, ParseException e) {
                        if (e == null) {
                            if (mAdapter != null) {
                                mAdapter.setPhotos(list);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private static class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.ViewHolder> {
        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView photoImageView;


            public ViewHolder(View itemView) {
                super(itemView);
                photoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            }
        }

        private ArrayList<Location> mPhotosList;
        private Context mContext;

        public PhotosRecyclerAdapter(ArrayList<Location> photosList, Context context) {
            mPhotosList = photosList;
            mContext = context;
        }

        @Override
        public PhotosRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_layout_photo, null);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final PhotosRecyclerAdapter.ViewHolder holder, int position) {
            Photo photo = mPhotosList.get(position).getPhoto();
            if (photo.getPhoto() != null && photo.getPhoto().getUrl() != null) {
                String photoUrl = photo.getPhoto().getUrl();

                Picasso.with(mContext)
                        .load(photoUrl)
                        .fit().centerCrop()
                        .into(holder.photoImageView);
            }
        }

        @Override
        public int getItemCount() {
            return mPhotosList.size();
        }

        public void setPhotos(List<Location> photos) {
            mPhotosList.clear();
            mPhotosList.addAll(photos);
        }
    }
}
