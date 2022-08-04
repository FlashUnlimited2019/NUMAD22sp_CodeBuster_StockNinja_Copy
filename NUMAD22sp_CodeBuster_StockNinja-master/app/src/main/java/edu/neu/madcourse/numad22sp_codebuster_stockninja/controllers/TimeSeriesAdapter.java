package edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.R;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.TimeSeriesAdapter.TimeSeriesHolder;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.TimeSeries;
import java.util.ArrayList;

public class TimeSeriesAdapter extends Adapter<TimeSeriesHolder> {

  private ArrayList<TimeSeries> timeSeries;

  public TimeSeriesAdapter() {
  }

  public TimeSeriesAdapter(ArrayList<TimeSeries> timeSeries) {
    this.timeSeries = timeSeries;
  }

  @NonNull
  @Override
  public TimeSeriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.time_series, parent, false);
    return new TimeSeriesHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TimeSeriesHolder holder, int position) {
    TimeSeries currTimeSeries = this.timeSeries.get(position);
    holder.txtTimestamp.setText(currTimeSeries.getTimestamp());
    holder.txtOpen.setText(String.valueOf(currTimeSeries.getOpen()));
    holder.txtHigh.setText(String.valueOf(currTimeSeries.getHigh()));
    holder.txtLow.setText(String.valueOf(currTimeSeries.getLow()));
    holder.txtClose.setText(String.valueOf(currTimeSeries.getClose()));
  }

  @Override
  public int getItemCount() {
    return this.timeSeries.size();
  }

  class TimeSeriesHolder extends ViewHolder {

    TextView txtTimestamp;
    TextView txtOpen;
    TextView txtHigh;
    TextView txtLow;
    TextView txtClose;

    public TimeSeriesHolder(@NonNull View view) {
      super(view);
      this.txtTimestamp = view.findViewById(R.id.txtTimestamp);
      this.txtOpen = view.findViewById(R.id.txtOpen);
      this.txtHigh = view.findViewById(R.id.txtHigh);
      this.txtLow = view.findViewById(R.id.txtLow);
      this.txtClose = view.findViewById(R.id.txtClose);
    }
  }
}
