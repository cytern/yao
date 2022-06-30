package com.cytern.util;

import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.NumberUtil;
import com.cytern.pojo.SimpleImageSub;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.img.ImgUtil.cut;

/**
 * 图片工具类
 */
public class RobotImageUtil{
    public static final String IMAGE_TYPE_GIF = "gif";// 图形交换格式
    public static final String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
    public static final String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
    public static final String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
    public static final String IMAGE_TYPE_PNG = "png";// 可移植网络图形
    public static final String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop

    /**
     * 图片属性为 复合图片 时 需要切分图片
     */
    public static List<String> subImageList(File sourceImage, SimpleImageSub simpleImageSub) {
        ArrayList<String> waitList = new ArrayList<>();
        //获取sourceImage的位置
        String sourceDir = sourceImage.getParentFile().getPath();
        File zipFile = new File(sourceDir + "/unzip");
        try {
            zipFile.delete();
            zipFile.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String parentDir = sourceDir  + "/unLoad";
        //生成解压文件夹
        File unLoadDir = new File(parentDir);
        try {
                unLoadDir.delete();
                unLoadDir.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //给源文件的文件名截取出来
        String imageName = sourceImage.getName().replace(".png", "")
                .replace(".jpg","")
                .replace(".jpeg","")
                .replace(".bmp","")
                .replace(".gif","")
                ;
        sliceByRowsAndCols(sourceImage, unLoadDir,simpleImageSub.getRow(),simpleImageSub.getCol());
        //给所有目录下的文件改名字 并且把完整目录返回
        try {
            Files.walkFileTree(unLoadDir.toPath(), new RobotFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File realFile = new File(sourceDir + "/unzip" +"/" + imageName + file.getFileName());
                    ImageIO.write(toBufferedImage(ImgUtil.getImage(file.toUri().toURL())),IMAGE_TYPE_PNG,realFile);
                    waitList.add(realFile.toPath().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.del(unLoadDir);
        return waitList;

    }
    public static void main(String[] args) {
        List<String> strings = RobotImageUtil.subImageList(FileUtil.file("E:\\projects\\javaProject\\dadi\\mods\\helly\\assets\\Gunther.png"), new SimpleImageSub(2, 2));
        System.out.println();
    }


    public static void sliceByRowsAndCols(File srcImageFile, File destDir, int rows, int cols) {
        try {
            sliceByRowsAndCols(ImageIO.read(srcImageFile), destDir, rows, cols);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
    /**
     * 图像切割（指定切片的行数和列数），默认RGB模式
     *
     * @param srcImage 源图像，如果非{@link BufferedImage}，则默认使用RGB模式
     * @param destDir  切片目标文件夹
     * @param rows     目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols     目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(Image srcImage, File destDir, int rows, int cols) {
        if (false == destDir.exists()) {
            FileUtil.mkdir(destDir);
        } else if (false == destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination Dir must be a Directory !");
        }

        try {
            if (rows <= 0 || rows > 20) {
                rows = 2; // 切片行数
            }
            if (cols <= 0 || cols > 20) {
                cols = 2; // 切片列数
            }
            // 读取源图像
            final BufferedImage bi = toBufferedImage(srcImage);
            int srcWidth = bi.getWidth(); // 源图宽度
            int srcHeight = bi.getHeight(); // 源图高度

            int destWidth = NumberUtil.partValue(srcWidth, cols); // 每张切片的宽度
            int destHeight = NumberUtil.partValue(srcHeight, rows); // 每张切片的高度

            // 循环建立切片
            Image tag;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    tag = cut(bi, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                    // 输出为文件
                    ImageIO.write(toRenderedImage(tag), IMAGE_TYPE_PNG, new File(destDir, "_r" + i + "_c" + j + ".png"));
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        return copyImage(img, BufferedImage.TYPE_4BYTE_ABGR);
    }


    /**
     * 将已有Image复制新的一份出来
     *
     * @param img       {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     */
    public static BufferedImage copyImage(Image img, int imageType) {
        return copyImage(img, imageType, null);
    }

    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        // ensures that all the pixels loaded
        // issue#1821@Github
        img = new ImageIcon(img).getImage();

        final BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), imageType);
        final Graphics2D bGr = GraphicsUtil.createGraphics(bimage, backgroundColor);
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }


    /**
     * {@link Image} 转 {@link RenderedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式。
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     * @since 4.3.2
     */
    public static RenderedImage toRenderedImage(Image img) {
        if (img instanceof RenderedImage) {
            return (RenderedImage) img;
        }

        return copyImage(img, BufferedImage.TYPE_4BYTE_ABGR);
    }



}
