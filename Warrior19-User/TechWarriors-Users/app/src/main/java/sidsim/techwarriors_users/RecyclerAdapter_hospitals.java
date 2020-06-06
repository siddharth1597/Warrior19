package sidsim.techwarriors_users;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerAdapter_hospitals extends RecyclerView.Adapter<RecyclerAdapter_hospitals.ViewHolder>  {
    private static ArrayList<StatusUpdateDetails> statusUpdateDetails;
    private Context context;
    private LayoutInflater mInflater;


    public RecyclerAdapter_hospitals(Context context, ArrayList<StatusUpdateDetails> results) {
        statusUpdateDetails = results;
        this.context = context;
    }


    public Object getItem(int position) {
        return statusUpdateDetails.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_design, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.name.setText(statusUpdateDetails.get(position).getName());
        holder.vac_beds.setText(String.valueOf(statusUpdateDetails.get(position).getVacantBeds()));
        holder.vac_vent.setText(String.valueOf(statusUpdateDetails.get(position).getVacantVentilaor()));


        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = statusUpdateDetails.get(position).getState();
                String city = statusUpdateDetails.get(position).getCity();
                int key = statusUpdateDetails.get(position).getKey();

                Intent in  = new Intent(context, Normal_View.class);
                in.putExtra("state", state);
                in.putExtra("city", city);
                in.putExtra("key", String.valueOf(key));
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return statusUpdateDetails.size();
    }

   public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,vac_vent,vac_beds;
        Button next;
       public ViewHolder(View itemView)
       {
           super(itemView);
           name = (TextView) itemView.findViewById(R.id.hospital_name);
           vac_beds = (TextView) itemView.findViewById(R.id.vacant_bed);
           vac_vent = (TextView) itemView.findViewById(R.id.vacant_vent);
           next = itemView.findViewById(R.id.next);
       }
       @Override
       public void onClick(View view)
       {
           Log.e("-----------------","clicked on----------");
       }
   }
}