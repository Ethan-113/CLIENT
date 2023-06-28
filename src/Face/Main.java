package Face;

import After.Token;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file1 = new File("D:\\workspace\\JAVA_CHAT\\CLIENT\\pic\\zy.jpg");
        File file2 = new File("D:\\测试用数据\\图片文件\\zy2.jpg");
        try {
            //String faceToken1 = Token.getToken("ml666");
            String faceToken1 = FaceUtil.detect(file1);
            String faceToken2 = "d7e2ab98c44e03deb06a6f141d8ab605";
            boolean res = FaceUtil.compareFace(faceToken1, faceToken2);
            System.out.println(res);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}