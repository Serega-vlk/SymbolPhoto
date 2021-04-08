
import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException{
        JButton open = new JButton();
        showStartMessage(open);
        String fileName = showFileChooserAndGetFileName(open);
        FileOutputStream fl = new FileOutputStream(getNewFileName(fileName));
        BufferedImage image = getImageByName(fileName);
        if (image == null) showErrorAndExit(open);
        WritableRaster writableRaster = image.getRaster();
        int compact = getCompactKof(writableRaster);
        System.out.println(writableRaster.getWidth() + " " + writableRaster.getHeight());
        try {
            drawTxtImage(writableRaster, image, fl, compact);
        } catch (IOException e){
            showErrorAndExit(open);
        }
        fl.close();
        Desktop.getDesktop().open(new File(getNewFileName(fileName)));
    }

    public static void showStartMessage(JComponent c){
        JOptionPane.showMessageDialog(c, "SymbolPhoto\n\n" +
                "1. Поместите файл с фото в ту же папку где и программа\n" +
                "2. Выберите этот файл\n" +
                "3. Программа сгенерирует .txt файл с вашим фото в этой папке\n" +
                "4. уменьшить размер текстового файла до 20% Для нормального просмотра изображения\n\n" +
                "автор: @serega_vlk");
    }

    public static String showFileChooserAndGetFileName(JComponent parent){
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(".\\"));
        fc.setDialogTitle("Выберите фото");
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("jpg") || f.getName().endsWith("png") || f.getName().endsWith("gif");
            }

            public String getDescription() {
                return "images";
            }
        });
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.showOpenDialog(parent);
        return fc.getSelectedFile().getAbsolutePath();
    }

    public static BufferedImage getImageByName(String fileName){
        File file = new File(fileName);
        try {
            return ImageIO.read(file);
        } catch (IOException e){
            return null;
        }
    }

    public static void showErrorAndExit(JComponent c){
        JOptionPane.showMessageDialog(c, "Произошла ошибка!");
        System.exit(-1);
    }

    public static String getNewFileName(String fileName){
        StringBuilder sb = new StringBuilder();
        String[] newFile = fileName.split("\\.");
        for (int i = 0; i < newFile.length; i++) {
            if (i == newFile.length - 1)
                sb.append("txt");
            else {
                sb.append(newFile[i]);
                sb.append('.');
            }
        }
        return sb.toString();
    }

    public static int getCompactKof(WritableRaster writableRaster){
        int compact = 1;
        if (writableRaster.getWidth() >= 1023 && writableRaster.getWidth() < 2047){
            compact += 1;
        }
        else if (writableRaster.getWidth() >= 2047 && writableRaster.getWidth() < 3071){
            compact += 2;
        }
        else if (writableRaster.getWidth() >= 3071 && writableRaster.getWidth() < 4095){
            compact += 3;
        }
        return compact;
    }

    public static void drawTxtImage(WritableRaster writableRaster, BufferedImage image, FileOutputStream fl, int compact) throws IOException{
        for (int i = 0; i < writableRaster.getHeight(); i = i + 2 * compact) {
            for (int j = 0; j < writableRaster.getWidth(); j = j + compact) {
                int[] pixel = writableRaster.getPixel(j, i, new int[4]);
                int sum = (pixel[0] + pixel[1] + pixel[2]) / 3;
                if (image.getType() == 6){
                    int alpha = 255 - pixel[3];
                    sum = sum + alpha;
                    if (sum > 255)
                        sum = 255;
                }
                fl.write(getSymbol(sum));
            }
            fl.write("\n".getBytes());
        }
    }

    public static char getSymbol(int sum){
        if (sum <= 15)
            return '@';
        else if (sum <= 30)
            return '%';
        else if (sum <= 45)
            return 'M';
        else if (sum <= 60)
            return '$';
        else if (sum <= 75)
            return '#';
        else if (sum <= 90)
            return '8';
        else if (sum <= 105)
            return 'P';
        else if (sum <= 120)
            return 'Z';
        else if (sum <= 135)
            return 'I';
        else if (sum <= 150)
            return 'L';
        else if (sum <= 165)
            return '+';
        else if (sum <= 180)
            return '*';
        else if (sum <= 195)
            return '/';
        else if (sum <= 210)
            return ',';
        else if (sum <= 225)
            return '^';
        else if (sum <= 240)
            return '.';
        else
            return ' ';
    }
}