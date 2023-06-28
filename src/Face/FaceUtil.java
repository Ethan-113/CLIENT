package Face;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class FaceUtil {
    private static HashMap<String, String> map = new HashMap<>();

    static {
        Properties pro = new Properties();
        InputStream in = FaceUtil.class.getResourceAsStream("api.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //读取配置文件
        map.put("api_key", pro.getProperty("API_KEY"));
        map.put("api_secret", pro.getProperty("API_SECRET"));
        map.put("display_name", pro.getProperty("DISPLAY_NAME"));
        map.put("outer_id", pro.getProperty("OUTER_ID"));
    }

    //用于对比两张照片是否是同一个人
    /*
    传入的faceToken1是第一张人脸的faceToken，faceToken2是第二张人脸的faceToken
    返回的是两张照片是否属于同一个人
    顺序无所谓，但是最好进行规定，第一个是存储好的照片的，第二个是用于比对的照片的
     */
    public static boolean compareFace(String faceToken1, String faceToken2) throws Exception {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/compare";
        map.put("face_token1", faceToken1);
        map.put("face_token2", faceToken2);

        byte[] bacd = HTTPUtil.post(url, map, null);
        String str = new String(bacd);
        JSONObject jsonObj = JSONObject.parseObject(str);
        JSONObject thresholdsObj = (JSONObject) jsonObj.get("thresholds");
        double lever_e5 = thresholdsObj.getDoubleValue("1e-5");
        double confidence = jsonObj.getDoubleValue("confidence");
        if(confidence > lever_e5){
            return true;
        }
        return false;
    }

    //搜索用户是否存在于set中，后续设计时发现该方法不能满足要求，弃用
    /*
    传入的是图片的faceToken
    返回的是是否在faceSet中找到
     */
    public static boolean search(String FaceToken) throws Exception {
        String url = "https://api-cn.faceplusplus.com/facepp/v3/search";
        map.put("face_token", FaceToken);

        byte[] bacd = HTTPUtil.post(url, map, null);
        String str = new String(bacd);
        if(str.indexOf("error_message") != -1){
            return false;
        }

        JSONObject jsonObj = JSONObject.parseObject(str);
        JSONObject thresholdsObj = (JSONObject) jsonObj.get("thresholds");
        double lever_e5 = thresholdsObj.getDoubleValue("1e-5");
        JSONArray resArr = (JSONArray) jsonObj.get("results");
        if(resArr != null && resArr.size()>=1){
            JSONObject res = (JSONObject) resArr.get(0);
            double confidence = res.getDoubleValue("confidence");
            if(confidence>lever_e5){
                return true;
            }
        }

        return false;
    }

    //获得人脸的token
    /*
    传入的是图片的地址
    返回的是该图片的faceToken
    注意：只在图中只存在一张人脸时返回faceToken，否则返回为null
     */
    public static String detect(File file) throws Exception {
        byte[] buff = HTTPUtil.getBytesFromFile(file);
        String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";

        HashMap<String, byte[]> byteMap = new HashMap<>();
        byteMap.put("image_file", buff);
        byte[] bacd = HTTPUtil.post(url, map, byteMap);
        String str = new String(bacd);
        //System.out.println(str);
        if(str.indexOf("error_message") != -1){
            return null;
        }
        JSONObject jsonObj = JSONObject.parseObject(str);
        int num = jsonObj.getIntValue("face_num");
        if(num == 1){
            JSONArray array = (JSONArray) jsonObj.get("faces");
            JSONObject face = (JSONObject) array.get(0);
            String faceToken = face.getString("face_token");
            return faceToken;
        }
        return null;
    }

    //创建FaceSet
    public static boolean createFaceSet() throws Exception{
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";

        byte[] bacd = HTTPUtil.post(url, map, null);
        String str = new String(bacd);
        //System.out.println(str);
        if(str.indexOf("error_message") != -1){
            return false;
        }
        return true;
    }

    //FaceSet是否存在
    public static boolean getDetail() throws Exception{
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";

        byte[] bacd = HTTPUtil.post(url, map, null);
        String str = new String(bacd);
        //System.out.println(str);
        if(str.indexOf("error_message") != -1){
            return false;
        }
        return true;
    }

    //往faceSet中加入人脸信息
    /*
    传入的是人脸的faceToken
    返回的是是否添加成功
     */
    public static boolean addFace(String FaceToken) throws Exception{
        boolean res = getDetail();
        //如果没有指定人脸集合，就进行创建
        if(!res){
            res = createFaceSet();
        }
        String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";
        map.put("face_tokens", FaceToken);

        byte[] bacd = HTTPUtil.post(url, map, null);
        String str = new String(bacd);
        //System.out.println(str);
        if(str.indexOf("error_message") != -1){
            return false;
        }
        return true;
    }
}
