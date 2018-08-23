package com.project.bottomup.upsa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MarkerFragment extends DialogFragment {
    ArrayList<DocumentInfo> markerClick = new ArrayList<DocumentInfo>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<Integer> id = new ArrayList<Integer>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_marker, null);
        builder.setView(view);
        try {
            Bundle bundle = this.getArguments();
            markerClick = bundle.getParcelableArrayList("markerClick");

            for (int i = 0; i < markerClick.size(); i++) {
                Log.i("MarkerFragment", "가게이름" + (i+1) + " : " + markerClick.get(i).getPlaceName());
                name.add(i,markerClick.get(i).getPlaceName());
                id.add(i,markerClick.get(i).getPlaceId());
            }
        }catch(Exception e){
            Toast.makeText(getActivity(), "등록된 장소(시설)가 없습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,name);

        // listView 생성 및 adapter 지정
        final ListView listview = (ListView)view.findViewById(R.id.markerContainer) ;
        listview.setAdapter(adapter);

        return builder.create();
    }
}
