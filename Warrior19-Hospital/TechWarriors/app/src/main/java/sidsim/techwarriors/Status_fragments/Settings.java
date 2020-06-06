package sidsim.techwarriors.Status_fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import sidsim.techwarriors.Login;
import sidsim.techwarriors.R;

public class Settings extends Fragment {
    ListView list;
    String[] items = {"About app", "Logout"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.settings, container, false);

        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.listview, items);
        list = v.findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {

                }
                else
                {
                    Intent in = new Intent(getContext(),Login.class);
                    startActivity(in);
                    getActivity().finish();
                }
            }
        });
        return v;
    }

}
