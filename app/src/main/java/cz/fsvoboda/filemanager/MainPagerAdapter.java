package cz.fsvoboda.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Item> dirs;
    private MainFragment currentFragment;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        dirs = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String startupPath = prefs.getString("default_folder", "/");
        Item startupDir = new Item(startupPath, startupPath);
        if (startupDir.getPath() == startupPath) { // existing path
            String[] split = startupPath.split("/", 0);
            String prevPath = "";
            dirs.add(new Item("/", "/"));
            for (String s : split) {
                if (s.equals("")) continue;
                prevPath += "/" + s;
                dirs.add(new Item(s, prevPath));
            }
        }
    }

    @Override
    public int getCount() {
        return dirs.size();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        args.putString("path", dirs.get(position).getPath());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dirs.get(position).getName();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public int addDir(Item item) {
        dirs.add(item);
        notifyDataSetChanged();
        return dirs.size();
    }

    public void removeLast() {
        dirs.remove(dirs.size()-1);
        notifyDataSetChanged();
    }
    public String getCurrentPath() {
        return dirs.get(dirs.size() - 1).getPath();
    }

    public MainFragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((MainFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
}
