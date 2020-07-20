package com.example.blooddonationorganization.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.text.format.DateUtils;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationorganization.Activities.LoginActivity;
import com.example.blooddonationorganization.Activities.MainActivity;
import com.example.blooddonationorganization.Activities.ProfileActivity;
import com.example.blooddonationorganization.Activities.UploadActivity;
import com.example.blooddonationorganization.Model.DonorDataModel;
import com.example.blooddonationorganization.Model.RequestDataModel;
import com.example.blooddonationorganization.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<RequestDataModel> donorList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private RequestDataModel mRequestDataModel;
    private CollectionReference collectionReference = db.collection("Requests");

    public ProfileAdapter(Context context, List<RequestDataModel> donorList) {
        this.mContext = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_posts, parent, false);


        return new ViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        RequestDataModel requestDataModel = donorList.get(position);
        String imageUrl;
        final String sRequest = donorList.get(position).getRequest();

        holder.request.setText(requestDataModel.getRequest());
        //viewHolder.name.setText(requestDataModel.getUsername());
        //holder.city.setText(requestDataModel.getCity());
        imageUrl = requestDataModel.getImageUrl();
        //1 hour ago..
        //Source: https://medium.com/@shaktisinh/time-a-go-in-android-8bad8b171f87
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(requestDataModel
                .getTimeAdded()
                .getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);


        /*
         Use Picasso library to download and show image
         */
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(holder.image);
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.image.getDrawable();
                if(bitmapDrawable == null)
                {
                    shareTextOnly(sRequest);
                }else
                {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(sRequest,bitmap);
                }
            }

        });
    }
    private void shareImageAndText(String sRequest, Bitmap bitmap) {
        Uri uri = savedImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,sRequest);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject Here");
        intent.setType("image/png");
        try {
            mContext.startActivity(Intent.createChooser(intent,"Share Via"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Uri savedImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(),"Images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder,"shared_images.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext,"com.example.blooddonationorganization.fileprovider",file);

        }
        catch (Exception e)
        {
            Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String sRequest) {
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT,sRequest);
        mContext.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }


    @Override
    public int getItemCount() {
        return donorList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView
                request,
                city,
                dateAdded,
                name;
        public ImageView image;
        public ImageView shareButton;
        String userId;
        String username;
        public Button deleteButton;

        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            mContext = ctx;

            request = itemView.findViewById(R.id.requestProfile_text);
            //city = itemView.findViewById(R.id.request_row_city);
            dateAdded = itemView.findViewById(R.id.dateTextView);
            image = itemView.findViewById(R.id.cardProfileImage);
            shareButton = (ImageView) itemView.findViewById(R.id.share_button);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            RequestDataModel requestDataModel = new RequestDataModel();
            Timestamp ID = donorList.get(getAdapterPosition()).getTimeAdded();
            if (view.getId() == R.id.deleteButton) {//delete item
                deletePost(ID);
            }

        }

        private void deletePost(final Timestamp id) {
            builder = new AlertDialog.Builder(mContext);

            inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    collectionReference.document(String.valueOf(id)).
                            delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                donorList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                dialog.dismiss();
                            }

                        }
                    });

                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }
}
