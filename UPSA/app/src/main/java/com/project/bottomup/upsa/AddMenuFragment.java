package com.project.bottomup.upsa;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class AddMenuFragment extends DialogFragment {
    private EditText menuName;
    private EditText menuPrice;
    private MenuInputListener listener;

    public static AddMenuFragment newInstance(MenuInputListener listener) {
        AddMenuFragment fragment = new AddMenuFragment();
        fragment.listener = listener;
        return fragment;
    }

    public interface MenuInputListener
    {
        void onMenuInputComplete(String name, int price);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_menu, null);
        menuName = (EditText)view.findViewById(R.id.menuNameText);
        menuPrice = (EditText)view.findViewById(R.id.menuPriceText);
        builder.setView(view)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                try{
                                    if(menuName.getText().length()>0 && menuPrice.getText().length()>0) {
                                        listener.onMenuInputComplete(menuName
                                                .getText().toString(), Integer.parseInt(menuPrice.getText().toString()));
                                    }else{
                                        throw new Exception();
                                    }
                                }catch(Exception e){
                                    Log.i("AddMenu","inputError");
                                    Toast.makeText(getActivity(),"이름(또는 가격)을 입력하지 않으셨습니다.",Toast.LENGTH_LONG).show();
                                }
                            }

                        }).setNegativeButton("취소", null);
        return builder.create();
    }
}
