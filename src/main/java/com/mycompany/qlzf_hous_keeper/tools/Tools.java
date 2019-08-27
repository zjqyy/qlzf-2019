package com.mycompany.qlzf_hous_keeper.tools;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jieyan
 */
public class Tools {

    /**
     * 格式化double数字
     *
     * @param value double值
     * @param min 最小小数位数
     * @param max 最大小数位数
     * @return String
     */
    public static String formatDouble(double value, int min, int max) {
        try {
            String retValue = null;
            NumberFormat format = NumberFormat.getInstance();
            format.setMinimumFractionDigits(min);
            format.setMaximumFractionDigits(max);
            format.setGroupingUsed(false);
            retValue = format.format(value);
            return retValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean check_null(Object info) {
        if (null == info || ("".equals(info))) {
            return true;
        }
        return false;
    }
    public static String getRandNum(int num) {
        String randNum = new Random().nextInt(1000000) + "";
        System.out.println("生成" + randNum);
        if (randNum.length() != num) {   //如果生成的不是6位数随机数则返回该方法继续生成
            return getRandNum(num);
        }
        return randNum;
    }

    public static String MD5(String sourceStr,String user_id) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());//更新摘要，返回byte数组
            byte b[] = md.digest();//执行诸如填充之类的最终操作完成哈希计算，重置摘要
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            buf.append(user_id);
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }
}
