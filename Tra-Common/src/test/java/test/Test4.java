package test;

import java.util.*;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-27 10:22
 **/
public class Test4 {
    public static int solution(int[] a) {
        int leftSum = 0,rightSum = 0;
        Set<Integer> l=new HashSet<>();
        Set<Integer> r=new HashSet<>();
        for (int ai : a) {
            //leftSum =leftSum+ ai;
            l.add(ai);
        }
        int minDiff = Integer.MAX_VALUE;
        for (int p = a.length - 1; p >= 0; p--) {
            //rightSum = rightSum+a[p];
            r.add(a[p]);
            //leftSum = leftSum-a[p];
            l.remove(a[p]);
            int diff = Math.abs( Collections.max(l) - Collections.max(r));
            if (diff == 0) {
                return 0;
            } else if (diff < minDiff) {
                minDiff = diff;
            }
        }
         System.out.println(r.size());
        return minDiff;
    }

    public static void main(String[] args){
        int[] A = {1,3,-3};
        System.out.println( solution(A));
    }

}
