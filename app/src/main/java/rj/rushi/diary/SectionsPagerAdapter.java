package rj.rushi.diary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Topics();
            case 1:
                return new Scribbles();
                /*
            case 0:
                return new Todo();
            case 1:
                return new Topics();
            case 2:
                return new Scribbles();
                 */
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
        //return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "TOPICS";
            case 1:
                return "SCRIBBLES";
                /*
            case 0:
                return "TODO";
            case 1:
                return "TOPICS";
            case 2:
                ret     urn "SCRIBBLES";
                */
        }
        return null;
    }
}
