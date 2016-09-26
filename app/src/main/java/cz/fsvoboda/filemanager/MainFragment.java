package cz.fsvoboda.filemanager;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A fragment representing a list of Items.
 */
public class MainFragment extends Fragment implements View.OnLongClickListener {

    private int mColumnCount = 2;
    private Context mListener;

    public RecyclerView recyclerView;
    private DirectoryRecyclerViewAdapter recyclerViewAdapter;

    private String path;
    public boolean isActionMode = false;
    private ViewPager viewPager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        path = "/";

        if (getArguments() != null) {
            path = getArguments().getString("path");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
            try {
                new SetAdapterTask().execute(path);
            } catch (Exception e) {

            }
            this.recyclerView = recyclerView;
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            mListener = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.pager);
        String path = ((MainPagerAdapter) vp.getAdapter()).getCurrentPath();
        //recyclerView.swapAdapter(new DirectoryRecyclerViewAdapter(createFileList(path), vp,this), true);
        try {
            new ReloadAdapterData().execute(path);
        }catch (Exception e) {

        }
    }

    @Override
    public boolean onLongClick(View view) {
        ((MainActivity)getActivity()).startSupportActionMode(actionModeCallback);

        int pos = recyclerView.getChildLayoutPosition(view);
        recyclerViewAdapter.toggleSelection(pos);

        return true;
    }

    private android.support.v7.view.ActionMode.Callback actionModeCallback = new android.support.v7.view.ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_context, menu);
            isActionMode = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    List<Integer> selectedItemPositions = recyclerViewAdapter.getSelectedItems();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        recyclerViewAdapter.removeData(selectedItemPositions.get(i));
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            recyclerViewAdapter.clearSelections();
            isActionMode = false;
        }
    };

    private class SetAdapterTask extends AsyncTask<String, Void, ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(String...path) {
            ArrayList<Item> set = new ArrayList<Item>();
            File dir = new File(path[0]);

            File[] list = dir.listFiles();
            if (list != null) {
                for (File file : list) {
                    set.add(new Item(file.getName(), file.getAbsolutePath()));
                }
            }
            Collections.sort(set, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if (o1.getType() == Item.Type.DIR && o2.getType() == Item.Type.FILE)
                        return -1;
                    if (o1.getType() == Item.Type.FILE && o2.getType() == Item.Type.DIR)
                        return 1;

                    return o1.compareTo(o2);
                }
            });
            recyclerViewAdapter = new DirectoryRecyclerViewAdapter(set, viewPager, MainFragment.this);
            return set;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            MainFragment.this.recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    private class ReloadAdapterData extends AsyncTask<String, Void, ArrayList<Item>> {
        @Override
        protected ArrayList<Item> doInBackground(String... path) {
            ArrayList<Item> set = new ArrayList<Item>();
            File dir = new File(path[0]);

            File[] list = dir.listFiles();
            if (list != null) {
                for (File file : list) {
                    set.add(new Item(file.getName(), file.getAbsolutePath()));
                }
            }
            Collections.sort(set, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if (o1.getType() == Item.Type.DIR && o2.getType() == Item.Type.FILE)
                        return -1;
                    if (o1.getType() == Item.Type.FILE && o2.getType() == Item.Type.DIR)
                        return 1;

                    return o1.compareTo(o2);
                }
            });
            return set;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            recyclerViewAdapter.swapDataSet(items);
        }
    }
}
