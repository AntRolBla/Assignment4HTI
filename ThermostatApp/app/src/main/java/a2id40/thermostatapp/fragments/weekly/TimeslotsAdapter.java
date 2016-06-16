package a2id40.thermostatapp.fragments.weekly;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.fragments.weekly.Models.TimeslotModel;
import butterknife.BindView;

/**
 * Created by IsabelGomes on 11/06/16.
 */
public class TimeslotsAdapter extends RecyclerView.Adapter<TimeslotsAdapter.TimeslotsViewHolder> {

    private ArrayList<TimeslotModel> mTimeslotsList;
    private TimeslotAdapterInterface mListener;

    public TimeslotsAdapter(ArrayList<TimeslotModel> mTimeslotsList, TimeslotAdapterInterface mListener) {
        this.mTimeslotsList = mTimeslotsList;
        this.mListener = mListener;
    }

    public class TimeslotsViewHolder extends RecyclerView.ViewHolder {
        public TextView timeslot;
        public ImageView dayNightImageView, deleteImageView;

        public TimeslotsViewHolder(View view) {
            super(view);
            timeslot = (TextView) view.findViewById(R.id.timeslots_recycler_row_timeslot_textview);
            dayNightImageView = (ImageView) view.findViewById(R.id.timeslots_recycler_row_day_night_imageview);
            deleteImageView = (ImageView) view.findViewById(R.id.timeslots_recycler_row_delete_imageview);
        }
    }

    public void updateTimeslotList(ArrayList<TimeslotModel> timeslotModelArrays){
        mTimeslotsList.clear();
        mTimeslotsList.addAll(timeslotModelArrays);
//        notifyDataSetChanged();
    }

    @Override
    public TimeslotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeslots_recycler_row, parent, false);

        return new TimeslotsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimeslotsViewHolder holder, final int position) {
        holder.timeslot.setText(mTimeslotsList.get(position).getmHourTimeslot());
        if (mTimeslotsList.get(position).getmDay()){
            holder.dayNightImageView.setImageResource(R.drawable.sun_image);
        } else {
            holder.dayNightImageView.setImageResource(R.drawable.moon_image);
            holder.deleteImageView.setVisibility(View.GONE);
        }
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.removeTimeslotClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTimeslotsList.size();
    }
}
