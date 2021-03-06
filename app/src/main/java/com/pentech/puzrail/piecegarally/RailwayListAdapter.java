package com.pentech.puzrail.piecegarally;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pentech.puzrail.R;
import com.pentech.puzrail.database.DBAdapter;
import com.pentech.puzrail.database.Line;
import com.pentech.puzrail.ui.MultiButtonListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by takashi on 2016/12/19.
 */

public class RailwayListAdapter extends BaseAdapter {
    private static String TAG = "RailwayListAdapter";
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Line> lines;

    public RailwayListAdapter(Context context, ArrayList<Line> lines){
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.lines = lines;
    }

    @Override
    public int getCount() {
        return this.lines.size();
    }

    @Override
    public Object getItem(int i) {
        return this.lines.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.lines.get(i).getDrawableResourceId();
    }

    private class ViewHolder {
        ImageView railwayLineImage;
        ImageView pieceBorderImage;
        TextView railwayLineName;
        TextView railwaylineKana;
        TextView silhouetteScoreAndUnit;

        ImageButton mapImageBtn;
        ImageButton staImageBtn;

        TextView locationScoreAndUnit;
        TextView locationTime;
        TextView stationsScoreAndUnit;
        TextView stationsProgress;
        TextView lineTotalScoreAndUnit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        MultiButtonListView list = null;

        Line line = lines.get(position);
        int companyId = line.getCompanyId();
        int lineId = line.getLineId();

        try{
            list = (MultiButtonListView)parent;
        }catch(Exception e){
            e.printStackTrace();
        }

        if(convertView == null){
            convertView = this.inflater.inflate(R.layout.railway_list_item,parent,false);
            holder = new ViewHolder();
            holder.pieceBorderImage = (ImageView)convertView.findViewById(R.id.piece_border_list_image_view);
            holder.railwayLineImage = (ImageView)convertView.findViewById(R.id.railway_line_list_image_view);
            holder.silhouetteScoreAndUnit = (TextView)convertView.findViewById(R.id.silhouetteScoreAndUnit);

            holder.railwayLineName = (TextView) convertView.findViewById(R.id.linename);
            holder.railwaylineKana = (TextView) convertView.findViewById(R.id.linekana);

            holder.mapImageBtn = (ImageButton)convertView.findViewById(R.id.mapImageButton);
            holder.staImageBtn = (ImageButton)convertView.findViewById(R.id.stationImageButton);
            holder.mapImageBtn.setOnClickListener(list);
            holder.staImageBtn.setOnClickListener(list);

            holder.locationScoreAndUnit = (TextView) convertView.findViewById(R.id.locationScoreAndUnit);
            holder.locationTime = (TextView) convertView.findViewById(R.id.locationTime);
            holder.stationsScoreAndUnit = (TextView) convertView.findViewById(R.id.stationsScoreAndUnit);
            holder.stationsProgress = (TextView) convertView.findViewById(R.id.stationsProgress);
            holder.lineTotalScoreAndUnit = (TextView) convertView.findViewById(R.id.lineTotalScoreAndUnit);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        // 路線名のテキスト表示
        Log.d(TAG,String.format("linename = %s, linekana = %s",line.getRawName(),line.getRawKana()));
        holder.railwayLineName.setText(line.getRawName()+" ");
        holder.railwaylineKana.setText("("+line.getRawKana()+")");
        holder.railwayLineName.setTextColor(Color.parseColor("#142d81"));
        holder.railwaylineKana.setTextColor(Color.parseColor("#142d81"));

        // 路線シルエットのイメージ表示
        Drawable drawable;
        if(line.isSilhouetteCompleted()){
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),line.getDrawableResourceId(),null);
        }
        else{
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.ic_line_question,null);
        }
        holder.railwayLineImage.setImageDrawable(drawable);

        // 「地図合わせ」のImageButtonの表示Image切り替え
        if(line.isLocationCompleted()){
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.tracklaying_completed_button,null);
        }
        else if(line.isSilhouetteCompleted()){
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.tracklaying_button,null);
        }
        else{
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.ic_tracklaying_inhibit,null);
        }
        holder.mapImageBtn.setImageDrawable(drawable);
        holder.mapImageBtn.setTag(position);

        // 「駅並べ」のImageButtonの表示Image切り替え
        int totalStationsInLine = line.getTotalStationsInLine();
        int answeredStationsInLine = line.getAnsweredStationsInLine();
        if(totalStationsInLine==answeredStationsInLine)
        {
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.station_open_completed_button,null);
        }
        else if(line.isSilhouetteCompleted()){
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.station_open_button,null);
        }
        else{
            drawable = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.ic_station_open_inhibit,null);
        }
        holder.staImageBtn.setImageDrawable(drawable);
        holder.staImageBtn.setTag(position);

        // silhouetteScoreの表示
        holder.silhouetteScoreAndUnit.setText(String.format("%d pt.",line.getSilhouetteScore()));
        // locationScoreの表示
        holder.locationScoreAndUnit.setText(String.format("%d pt.",line.getLocationScore()));
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        long elapseTime = line.getLocationTime() * 1000;
        String dispTime = sdf.format(elapseTime);
        holder.locationTime.setText(dispTime);

        // stationsScoreの表示
        holder.stationsScoreAndUnit.setText(String.format("%d pt.",line.getStationsTotalScore()));
        // 「駅並べ」の進捗表示
        holder.stationsProgress.setText(String.format("%d/%d",answeredStationsInLine,totalStationsInLine));
        // 路線合計得点の表示
        int lineTotalScore = line.getSilhouetteScore() + line.getLocationScore() + line.getStationsTotalScore();
        holder.lineTotalScoreAndUnit.setText(String.format("%d pt.",lineTotalScore));

        return convertView;
    }
}
