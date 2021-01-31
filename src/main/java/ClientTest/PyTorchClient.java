/**
 * Copyright (c) 2020-present, Wuba, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ClientTest;

import com.bj58.ailab.dlpredictonline.grpc.WpaiDLPredictOnlineServiceGrpc;
import com.bj58.ailab.dlpredictonline.entity.PredictionProtos;
import com.google.protobuf.ByteString;
import com.google.protobuf.Value;
import java.awt.Color;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * PyTorch 图像数字识别模型示例
 * 
 * @author 58 模型文件位于 demo/model/pytorch
 **/
public class PyTorchClient {

    /**
     * 读取图片
     */
    public static byte[] readImageFile(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] content = out.toByteArray();
        return content;
    }

    public PredictionProtos.SeldonMessage getRequest(byte[] content,int Id) {
       
        ByteString bsdata = ByteString.copyFrom(content);

        Map<String, Value> tagsMap = new HashMap<>(1);
        Value taskId = Value.newBuilder().setNumberValue(Id).build();
        tagsMap.put("taskid", taskId);

        PredictionProtos.Meta meta = PredictionProtos.Meta.newBuilder().putAllTags(tagsMap).build();

        PredictionProtos.SeldonMessage request = PredictionProtos.SeldonMessage.newBuilder().setMeta(meta)
                .setBinData(bsdata).build();

        return request;
    }

    public static void writeImageFromArray(String imageFile, String type, double[][] rgbArray){
        // get image width and height
        int width = rgbArray[0].length;
        int height = rgbArray.length;

        // write data to BufferedImage
        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        int frontCol = new Color(0, 245, 255).getRGB();
        int backCol = new Color(0, 0, 0).getRGB();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (rgbArray [i][j] == 1) {
                    bf.setRGB(j, i, frontCol);
                } else {
                    bf.setRGB(j, i, backCol);
                }
            }
        }

        // write image
        try {
         File file= new File(imageFile);
         ImageIO.write((RenderedImage)bf, type, file);
        } catch (IOException e) {
         e.printStackTrace();
        }
       }
    public void saveResult(PredictionProtos.SeldonMessage response, String img_name,String savePath) {
        if (response != null) {

            try {
                int dotIndex = img_name.lastIndexOf('.');
                img_name = (dotIndex == -1) ? img_name : img_name.substring(0, dotIndex);
                
                String file_name = savePath + '/' + img_name + ".png";
                List<Double> temp = response.getData().getTensor().getValuesList();
                int width = 256;
                int height = 256;
                double[][] array = new double[width][height];
                for(int i=0;i<width;i++){
                   for(int j=0;j<height;j++){
                       array[i][j] = temp.get(i*height+j);
                    }
                }
                writeImageFromArray(file_name, "png", array);
      
            } catch (Exception e) {  
                e.printStackTrace();  

            }
        }else{
            System.out.println("response is null");
        }
    }

    public static void client(WpaiDLPredictOnlineServiceGrpc.WpaiDLPredictOnlineServiceBlockingStub blockingStub,int taskId,String imagePath,String savePath){
      
        System.out.println(System.getProperty("user.dir"));
        PyTorchClient pyTorchClient = new PyTorchClient();
        File dataDir = new File(imagePath);
        List<String> dataPathlist = new ArrayList<>();
        listDir(dataDir,dataPathlist);
        for (String filePath : dataPathlist ){
            byte[] content = new byte[0];
            File file = new File(filePath);
            try {
                content = readImageFile(file);
                System.out.println("length: " + content.length);
                if (content.length <3000 | content.length > 700000){
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            PredictionProtos.SeldonMessage request = pyTorchClient.getRequest(content,taskId);
            PredictionProtos.SeldonMessage response = blockingStub.withDeadlineAfter(10000000, TimeUnit.MILLISECONDS).pytorchPredict(request);
            String [] sFilePath = filePath.split("/",0);
            int num =sFilePath.length;
            
            String savePath2=savePath+'/'+sFilePath[num-3]+'/'+sFilePath[num-2];
            File path =new File(savePath2);
            // 如果文件夹不存在则创建
            if  (!path .exists()  && !path .isDirectory())  {       
                path .mkdirs();    
            } 
               pyTorchClient.saveResult(response,file.getName(),savePath2);  
           
        }
    }
    
        /**
     * 列出当前路径下的所有文件路径
     * @param file
     */
    public static void listDir(File file,List<String> list){
        if(file.isDirectory()){	 // 是一个目录
            // 列出目录中的全部内容
            File results[] = file.listFiles();
            if(results != null){
                for(int i=0;i<results.length;i++){
                    listDir(results[i],list);	// 继续一次判断
                }
            }
        }else{	// 是文件
            String fileStr = (file.getName()).toString();
            String fileFormat = "tif";
            String suffixStr = "";
            if(null != fileStr && !"".equals(fileStr)){
                suffixStr = fileStr.substring(fileStr.lastIndexOf(".")+1,
                        fileStr.length());
                if(fileFormat.toUpperCase().indexOf(suffixStr.toUpperCase()) != -1){
                   list.add(file.getPath());
                }
            }
        }
    }
}
