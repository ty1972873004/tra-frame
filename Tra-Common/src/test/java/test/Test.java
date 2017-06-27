package test;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-27 9:25
 **/
public class Test {
    public static int solution(int[] A) {
        int l = A.length;
        if(l < 3){
           return 0;
        }
        int ans = 0;
        for(int i = 0;i<l - 1;i++)
        {
            int d=A[i+1]-A[i];
            int k=1;
            while(i+k+1 < l  && A[i+k+1] - A[i+k] ==d)
            {
               k++;
            }
            if(k > 1)
            {
                ans +=k - 1;
            }
        }
        return ans;
    }

    public static void main(String[] args){
        int[] A = {-1,1,3,3,3,2,1,0};
         System.out.println( solution(A));

    }

}
