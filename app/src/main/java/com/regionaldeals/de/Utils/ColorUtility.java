package com.dealspok.dealspok.Utils;

/**
 * Created by Umi on 28.12.2017.
 */
import com.dealspok.dealspok.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tulsi on 3/10/15.
 */
public class ColorUtility {

    public static int getColorFromPosition(int position) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(R.color.pantone9);
        list.add(R.color.pantone6);
        list.add(R.color.pantone3);
        list.add(R.color.pantone0);
        list.add(R.color.pantone5);
        list.add(R.color.pantone2);
        list.add(R.color.pantone7);
        list.add(R.color.pantone8);
        list.add(R.color.pantone1);
        list.add(R.color.pantone4);

        switch (position%10){
            case 0: return list.get(0);
            case 1: return list.get(1);
            case 2: return list.get(2);
            case 3: return list.get(3);
            case 4: return list.get(4);
            case 5: return list.get(5);
            case 6: return list.get(6);
            case 7: return list.get(7);
            case 8: return list.get(8);
            case 9: return list.get(9);
            default: return list.get(0);
        }
    }

    public static int getRandomColor() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(R.color.pantone9);
        list.add(R.color.pantone6);
        list.add(R.color.pantone3);
        list.add(R.color.pantone0);
        list.add(R.color.pantone5);
        list.add(R.color.pantone2);
        list.add(R.color.pantone7);
        list.add(R.color.pantone8);
        list.add(R.color.pantone1);
        list.add(R.color.pantone4);
        return list.get(new Random().nextInt(10));
    }
}
