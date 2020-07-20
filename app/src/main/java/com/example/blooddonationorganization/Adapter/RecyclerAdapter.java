package com.example.blooddonationorganization.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationorganization.Activities.MainActivity;
import com.example.blooddonationorganization.BuildConfig;
import com.example.blooddonationorganization.Model.DonorDataModel;
import com.example.blooddonationorganization.Model.RequestDataModel;
import com.example.blooddonationorganization.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<RequestDataModel> donorList;

    public RecyclerAdapter(Context context, List<RequestDataModel> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.request_item, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder viewHolder, final int position) {

        final RequestDataModel requestDataModel = donorList.get(position);
        final String imageUrl;
        final String sCity = donorList.get(position).getCity();
        final String sRequest = donorList.get(position).getRequest();
        final String sMbl = donorList.get(position).getMobile();


        viewHolder.request.setText(requestDataModel.getRequest());
        //viewHolder.name.setText(requestDataModel.getUsername());
        viewHolder.city.setText(requestDataModel.getCity().toUpperCase());
        imageUrl = requestDataModel.getImageUrl();
        //1 hour ago..
        //Source: https://medium.com/@shaktisinh/time-a-go-in-android-8bad8b171f87
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(requestDataModel
                .getTimeAdded()
                .getSeconds() * 1000);
        viewHolder.dateAdded.setText(timeAgo);


        /*
         Use Picasso library to download and show image
         */
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(viewHolder.image);
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) viewHolder.image.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(sCity, sRequest);
                } else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(sCity, sRequest, bitmap);
                }
            }

        });

        viewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + sMbl));
                context.startActivity(intent);
            }
        });

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog builder = new Dialog(context);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.setCanceledOnTouchOutside(true);
                Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                BitmapDrawable bitmapDrawable = (BitmapDrawable) viewHolder.image.getDrawable();
                int a=view.getId();
                if(R.id.cardImage==a)
                {
                    File imageFolder = new File(context.getCacheDir(),"Images");
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Uri uri = savedImageToShare(bitmap);
                    imageFolder.mkdirs();

                    ImageView imageView = new ImageView(context);
                    imageView.setImageURI(uri);
                    builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.getWindow().setLayout(1000,1400);
                    builder.show();
                }
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Uri uri=null;
                        dialogInterface.dismiss();
                    }
                });

            }
        });
    }

    private void shareImageAndText(String sCity, String sRequest, Bitmap bitmap) {
        String shareBody = sCity + "\n" + sRequest;
        Uri uri = savedImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject Here");
        intent.setType("image/png");
        try {
            context.startActivity(Intent.createChooser(intent,"Share Via"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Uri savedImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(),"Images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder,"shared_images.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context,"com.example.blooddonationorganization.fileprovider",file);

        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String sCity, String sRequest) {
        String shareBody = sCity + "\n" + sRequest;
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        context.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                request,
                city,
                dateAdded,
                name;
        public ImageView image;
        public ImageView shareButton;
        public ImageView callButton;
        String userId;
        String username;


        public ViewHolder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context = ctx;

            request = itemView.findViewById(R.id.request_text);
            request.setTextIsSelectable(true);
            city = itemView.findViewById(R.id.request_row_city);
            dateAdded = itemView.findViewById(R.id.dateTextView);
            image = itemView.findViewById(R.id.cardImage);
            shareButton = (ImageView) itemView.findViewById(R.id.share_button);
            callButton = itemView.findViewById(R.id.call_button);


        }
    }

}

