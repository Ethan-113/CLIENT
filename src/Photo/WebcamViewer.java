package Photo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.DataBufferByte;
import java.io.File;
import javax.imageio.ImageIO;

public class WebcamViewer extends JPanel {
    private static final long serialVersionUID = 1L;
    private BufferedImage image;
    private boolean capturing;

    public WebcamViewer() {
        this.setPreferredSize(new Dimension(640, 480));
        this.capturing = false;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image != null) {
            g.drawImage(this.image, 0, 0, null);
        }
    }

    public static void photo() {
        // 加载OpenCV本机库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 创建窗口
        JFrame frame = new JFrame("图像采集");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // 创建图像显示面板
        WebcamViewer viewer = new WebcamViewer();
        frame.add(viewer);

        // 创建拍照按钮
        JButton captureButton = new JButton("拍照");
        captureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewer.setCapturing(true);
            }
        });
        frame.add(captureButton);

        // 打开摄像头
        VideoCapture videoCapture = new VideoCapture(0);

        // 检查摄像头是否打开成功
        if (!videoCapture.isOpened()) {
            System.out.println("无法打开摄像头");
            System.exit(-1);
        }

        // 循环读取并显示视频帧
        Mat mat = new Mat();
        MatOfByte buffer = new MatOfByte();
        while (true) {
            // 读取一帧
            if (!videoCapture.read(mat)) {
                System.out.println("无法读取摄像头帧！");
                break;
            }

            // 检查帧图像是否为空
            if (mat.empty()) {
                System.out.println("读取的帧图像为空！");
                break;
            }

            // 转换图像格式
            try {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
            } catch (Exception e) {
                System.out.println("图像格式转换错误！");
                e.printStackTrace();
                break;
            }

            // 将图像转换为BufferedImage
            byte[] data = new byte[mat.rows() * mat.cols() * (int) (mat.elemSize())];
            mat.get(0, 0, data);
            BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
            image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);

            // 设置图像到显示面板
            viewer.setImage(image);

            // 检查是否正在进行拍照
            if (viewer.isCapturing()) {
                // 保存照片到指定路径
                String outputPath = "pic\\zy.jpg"; // 指定保存路径和文件名
                File outputFile = new File(outputPath);
                try {
                    ImageIO.write(image, "JPG", outputFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("保存照片时发生错误！");
                }

                // 停止拍照，关闭摄像头和窗口
                viewer.setCapturing(false);
                videoCapture.release();
                frame.dispose();
                break;
            }

            // 重绘窗口
            frame.pack();
            frame.setVisible(true);
        }
    }
        public boolean isCapturing() {
        return capturing;
    }

    public void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }
}
