package com.mycompany.qlzf_hous_keeper.service.impl;

import com.google.gson.Gson;
import com.mycompany.qlzf_hous_keeper.service.PicService;
import com.mycompany.qlzf_hous_keeper.tools.DeleteFileUtil;
import com.mycompany.qlzf_hous_keeper.tools.OutData;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import com.mycompany.qlzf_hous_keeper.tools.Tools;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/15 10:13
 */
@Service
public class PicServiceImpl implements PicService {
    private final Gson gson = new Gson();

    @Override
    public Object publishPic(HttpServletResponse response, HttpServletRequest request) {
        PrintWriter out=null;
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload sfu = new ServletFileUpload(factory);
            sfu.setHeaderEncoding("UTF-8");//设置字符
            sfu.setFileSizeMax(1 * 1024 * 1024);//  1M
            sfu.setSizeMax(100 * 1024 * 1024);//上传文件最大150m   必须写在parseRequest之前
            List<FileItem> items = sfu.parseRequest(request);//获取上传的文件
            if (items.isEmpty()) {
                out.println(gson.toJson(OutData.softwareFormart("未获取到图片信息，请重新上传"), Map.class));
                out.flush();
                return false;
            }
            String file_name = null;
            List<Map> reList = new ArrayList<>();
            for (FileItem fileItem : items) {
                try {
                    file_name = fileItem.getFieldName();
                    InputStream in = fileItem.getInputStream();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    Thumbnails.of(in).size(108, 144).keepAspectRatio(false).toOutputStream(os);
                    InputStream in_deal = new ByteArrayInputStream(os.toByteArray());
                    String new_name = markImageByIcon(in_deal);
                    Map map = new HashMap();
                    map.put("file_name", file_name);
                    map.put("new_name", new_name);
                    if (null == new_name) {
                        map.put("status", false);
                    } else {
                        map.put("status", true);
                    }
                    reList.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    Map map = new HashMap();
                    map.put("file_name", file_name);
                    map.put("status", false);
                    reList.add(map);
                }
            }
            out.println(gson.toJson(OutData.software_Formart(reList), Map.class));
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            out.println(gson.toJson(OutData.softwareFormart("系统异常，请保证单个文件不超过1M"), Map.class));
            out.flush();
            return false;
        }
    }


    @Override
    public Object getShopPic(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        try {
            String zip = request.getParameter("zip");
            String pic_id = request.getParameter("picNum");
            String path = null;
            String Info = System.getProperties().toString();
            if (Info.contains("Windows")) {
                path = "C:" + File.separator + "zulin_pic" + File.separator;
            } else {
                path = File.separator + "home" + File.separator + "hadoop" + File.separator + "zulin_pic" + File.separator;
            }
            if (zip.equals("yes")) {
                out.write(get_zip_date(path + pic_id + ".jpg"));
            } else {
                out.write(get_nozip_date(path + pic_id + ".jpg"));
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统找不到指定的文件");
        } finally {
            out.flush();
        }
    }

    @Override
    public Object deletPic(HttpServletResponse response, HttpServletRequest request) {
        try {
            String path = null;
            String picNum = request.getParameter("picNum");
            if (Tools.check_null(picNum)) return OutData.softwareFormart("图片id不能为空");
            String Info = System.getProperties().toString();
            if (Info.contains("Windows")) {
                path = "C:" + File.separator + "zulin_pic" + File.separator;
            } else {
                path = File.separator + "home" + File.separator + "hadoop" + File.separator + "zulin_pic" + File.separator;
            }
            String fileDir = path + picNum;
            String message = DeleteFileUtil.delete(fileDir);
            if (message.equals("ok")) {
                return OutData.softwareFormart_OK();
            } else return OutData.softwareFormart(message);

        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }


    /**
     * 获取缩略图
     *
     * @param path
     * @return
     */
    private byte[] get_zip_date(String path) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(path);
            out = new ByteArrayOutputStream();
            Thumbnails.of(in).size(108, 144).toOutputStream(out);
            byte[] zip_pic = out.toByteArray();
            return zip_pic;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * 获取未压缩的文件
     *
     * @param path
     * @return
     */
    private byte[] get_nozip_date(String path) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream swapStream = null;
        try {
            in = new FileInputStream(path);
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int n = 0;
            while ((n = in.read(buff)) > 0) {
                swapStream.write(buff, 0, n);
            }
            byte[] data = swapStream.toByteArray();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
            if (swapStream != null) {
                try {
                    swapStream.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param input 源文件数据
     */
    private static String markImageByIcon(InputStream input) {
        try {
            String path = null;
            String tag_path = null;
            String Info = System.getProperties().toString();
            if (Info.contains("Windows")) {
                path = "C:" + File.separator + "zulin_pic" + File.separator;
                tag_path = "C:" + File.separator + "tag" + File.separator + "shuiYin.png";
            } else {
                path = File.separator + "home" + File.separator + "hadoop" + File.separator + "zulin_pic" + File.separator;
                tag_path = File.separator + "home" + File.separator + "hadoop" + File.separator + "tag" + File.separator + "shuiYin.png";
            }
            Image srcImg = ImageIO.read(input);
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
                    BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
                    0, null);
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(tag_path);
            // 得到Image对象。
            Image img = imgIcon.getImage();
            float alpha = 0.8f; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 表示水印图片的位置
            g.drawImage(img, 460, 390, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();

            Random random = new Random();
            int random_num = random.nextInt(999) % (999 - 100 + 1) + 100;
            String new_name = String.valueOf(System.currentTimeMillis()) + String.valueOf(random_num);

            File file = new File(path + new_name + ".jpg");
            while (file.exists()) {
                random_num = random.nextInt(999) % (999 - 100 + 1) + 100;
                new_name = String.valueOf(System.currentTimeMillis()) + String.valueOf(random_num);
                file = new File(path + new_name + ".jpg");
            }
            // 生成图片
            ImageIO.write(buffImg, "JPG", file);
            return new_name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != input) {
                    input.close();
                }
            } catch (Exception e) {
            }
        }
    }

}
