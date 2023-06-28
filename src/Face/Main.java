package Face;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file1 = new File("D:\\测试用数据\\图片文件\\ml1.jpg");
        File file2 = new File("D:\\测试用数据\\图片文件\\fly.jpg");
        try {
            String faceToken1 = FaceUtil.detect(file1);
            String faceToken2 = FaceUtil.detect(file2);
            boolean res = FaceUtil.compareFace(faceToken1, faceToken2);
            System.out.println(res);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}