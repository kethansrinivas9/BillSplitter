package com.example.billsplitter.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.billsplitter.DisplayGroup;
import com.example.billsplitter.R;
import com.example.billsplitter.SettleExpenses;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

//class for Group Tab
public class GroupsTab extends Fragment {
    ArrayList<String> groups;
    ArrayList<String> amounts;


    public GroupsTab () {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private HomeViewModel homeViewModel;
    private Integer imgId;
    private ListView listView;
    private TextView emptyGroup;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.groups_tab, container, false);
        groups = new ArrayList<>();
        amounts = new ArrayList<>();


        listView = view.findViewById(R.id.group_list);
        imgId = R.drawable.ic_group_black_24dp;
        emptyGroup = view.findViewById(R.id.empty_groups);


        //Populating the friends list
        homeViewModel.getGroupsList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                groups = strings;
                updateList();

            }
        });

        //Populating the friends list
        homeViewModel.getGroupsAmountList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                amounts = strings;
                updateList();

            }
        });

        return view;
    }

    private void updateList(){

       if (groups.isEmpty()){
            emptyGroup.setText(getString(R.string.empty_group));
       }

        final CustomListView adapter = new CustomListView(getActivity(), groups, amounts , imgId);
        listView.setAdapter(adapter);

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapter.getItem(i);
                Intent intent = new Intent(getContext(), SettleExpenses.class);
                intent.putExtra("UserName",item);
                startActivity(intent);
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String group_name = adapter.getItem(i);
                Intent intent = new Intent(getContext(), DisplayGroup.class);
                intent.putExtra("groupName",group_name);
                startActivity(intent);

            }
        });
    }

}
