package com.mycompany.qlzf_hous_keeper.tools;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/7/26 18:08
 */

import java.io.File;

/**
 * 删除文件或目录
 *
 * @author
 * @see Class
 * @since JDK1.8
 */

public class DeleteFileUtil {
    /**
     * 删除文件，可以是单个文件或文件夹
     *
     * @param fileName 待删除的文件名
     * @return 文件删除成功返回true, 否则返回false
     */
    public static String delete(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return "该图片不存在";
            } else if (file.isFile()) {
                file.delete();
                return "ok";
            } else {
                return "删除图片失败";
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 删除单个文件
     *
     * @param fileName 被删除文件的文件名
     * @return 单个文件删除成功返回true, 否则返回false
     */
    public static String deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            return "ok";
        } else {
            return "删除单个文件" + fileName + "失败！";
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dir 被删除目录的文件路径
     * @return 目录删除成功返回true, 否则返回false
     */
//    public static boolean deleteDirectory(String dir) {
//        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
//        if (!dir.endsWith(File.separator)) {
//            dir = dir + File.separator;
//        }
//        File dirFile = new File(dir);
//        // 如果dir对应的文件不存在，或者不是一个目录，则退出
//        if (!dirFile.exists() || !dirFile.isDirectory()) {
//            System.out.println("删除目录失败" + dir + "目录不存在！");
//            return false;
//        }
//        boolean flag = true;
//        // 删除文件夹下的所有文件(包括子目录)
//        File[] files = dirFile.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            // 删除子文件
//            if (files[i].isFile()) {
//                flag = deleteFile(files[i].getAbsolutePath());
//                if (!flag) {
//                    break;
//                }
//            }
//            // 删除子目录
//            else {
//                flag = deleteDirectory(files[i].getAbsolutePath());
//                if (!flag) {
//                    break;
//                }
//            }
//        }
//
//        if (!flag) {
//            System.out.println("删除目录失败");
//            return false;
//        }
//
//        // 删除当前目录
//        if (dirFile.delete()) {
//            System.out.println("删除目录" + dir + "成功！");
//            return true;
//        } else {
//            System.out.println("删除目录" + dir + "失败！");
//            return false;
//        }
//    }
    // 删除文件夹
    // param folderPath 文件夹完整绝对路径

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除指定文件夹下所有文件
    // param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        // String fileName = "g:/temp/xwz.txt";
        // DeleteFileUtil.deleteFile(fileName);
        String fileDir = "C:\\Users\\zjq\\Desktop\\FBDweb.war";
        // DeleteFileUtil.deleteDirectory(fileDir);
        DeleteFileUtil.delete(fileDir);
        DeleteFileUtil t = new DeleteFileUtil();
        delFolder("C:\\Users\\zjq\\Desktop\\Admin(17)");
        System.out.println("deleted");

    }
}
