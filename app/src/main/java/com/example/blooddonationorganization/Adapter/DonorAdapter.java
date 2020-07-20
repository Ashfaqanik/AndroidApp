package com.example.blooddonationorganization.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationorganization.Model.DonorDataModel;
import com.example.blooddonationorganization.R;

import java.text.MessageFormat;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {
    private Context mContext;
    private List<DonorDataModel> donorDetailList;

    public DonorAdapter(Context context, List<DonorDataModel> donorDetailList) {
        this.mContext = context;
        this.donorDetailList = donorDetailList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donors, parent, false);


        return new ViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorDataModel donorDataModel = donorDetailList.get(position);
        final String mName=donorDataModel.getUsername();
        final String mCity=donorDataModel.getCity();
        final String mBld=donorDataModel.getBloodGrp();
        final String mMbl=donorDataModel.getMobile();
        final String mEmail=donorDataModel.getEmail();

        holder.username.setText(MessageFormat.format("Name: {0}", donorDataModel.getUsername()));
        holder.userCity.setText(MessageFormat.format("City: {0}", donorDataModel.getCity()));
        holder.bloodGrp.setText(MessageFormat.format("Blood group: {0}",donorDataModel.getBloodGrp()));
        holder.phone.setText(MessageFormat.format("Contact no: {0}", donorDataModel.getMobile()));
        holder.email.setText(MessageFormat.format("Email Address: {0}", donorDataModel.getEmail()));
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    shareTextOnly(mName,mCity,mBld,mMbl,mEmail);

            }

        });
       holder.call.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:" + mMbl));
               mContext.startActivity(intent);
           }
       });
    }
    private void shareTextOnly(String mName, String mCity,String mBld, String mMbl,String mEmail) {
        String shareBody = "Name: " + mName + "\n" + "City: " + mCity + "\n" + "Blood group: " +mBld + "\n" +
                "Contact no: " + mMbl + "\n" + "Email Address: " + mEmail;
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        mContext.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    @Override
    public int getItemCount() {
        return donorDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                username,
                userCity,
                bloodGrp,
                phone,
                email;
        public ImageView share;
        public ImageView call;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            mContext = ctx;

            username = itemView.findViewById(R.id.userName);
            userCity = itemView.findViewById(R.id.userCity);
            bloodGrp = itemView.findViewById(R.id.bloodGroup);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            share = itemView.findViewById(R.id.share_button);
            call = itemView.findViewById(R.id.call_button);
        }
    }
}
