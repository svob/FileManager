package cz.fsvoboda.filemanager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryRecyclerViewAdapter extends RecyclerView.Adapter<DirectoryRecyclerViewAdapter.ViewHolder> {
    private ViewPager viewPager;
    private ArrayList<Item> dataSet;
    private MainPagerAdapter pagerAdapter;
    private MainFragment fragment;
    private SparseBooleanArray selectedItems;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView image;
        public LinearLayout layout;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.fileName);
            image = (ImageView) v.findViewById(R.id.icon);
            layout = (LinearLayout) v.findViewById(R.id.row_layout);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item item = dataSet.get(getLayoutPosition());

                    if (fragment.isActionMode) {
                        toggleSelection(getLayoutPosition());
                    } else {
                        if (item.getType() == Item.Type.FILE) {
                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            String mimeType = myMime.getMimeTypeFromExtension(item.extension());
                            i.setDataAndType(Uri.fromFile(new File(item.getPath())), mimeType);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                view.getContext().startActivity(i);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(view.getContext(), "Cannot open the file", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (item.canRead()) {
                                int newPos = pagerAdapter.addDir(item);
                                viewPager.setCurrentItem(newPos, true);
                            } else {
                                Toast.makeText(view.getContext(), "Cannot open the directory", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    public DirectoryRecyclerViewAdapter(ArrayList<Item> dataSet, ViewPager vp, MainFragment fragment) {
        this.dataSet = dataSet;
        this.viewPager = vp;
        this.pagerAdapter = (MainPagerAdapter) vp.getAdapter();
        this.fragment = fragment;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public DirectoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_main_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(dataSet.get(position).getName());
        if (dataSet.get(position).getType() == Item.Type.DIR)
            holder.image.setImageResource(R.drawable.dir_icon);
        else
        holder.image.setImageResource(R.drawable.file_icon);
        holder.layout.setOnLongClickListener(fragment);
        holder.itemView.setActivated(selectedItems.get(position, false));
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false))
            selectedItems.delete(pos);
        else
            selectedItems.put(pos, true);

        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        dataSet.get(position).delete();
        dataSet.remove(position);
        notifyItemRemoved(position);
    }

    public void swapDataSet(ArrayList<Item> fileList) {
        dataSet.clear();
        dataSet.addAll(fileList);
        notifyDataSetChanged();
    }
}
