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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.Spring;
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

    public PredictionProtos.SeldonMessage getRequest(File file) {
        byte[] content = new byte[0];
        try {
            content = readImageFile(file);
            System.out.println("length: " + content.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteString bsdata = ByteString.copyFrom(content);

        Map<String, Value> tagsMap = new HashMap<>(1);
        Value taskId = Value.newBuilder().setNumberValue(67).build();
        tagsMap.put("taskid", taskId);

        PredictionProtos.Meta meta = PredictionProtos.Meta.newBuilder().putAllTags(tagsMap).build();

        PredictionProtos.SeldonMessage request = PredictionProtos.SeldonMessage.newBuilder().setMeta(meta)
                .setBinData(bsdata).build();

        return request;
    }

    public void saveResult(PredictionProtos.SeldonMessage response,String img_name){
        if (response != null) {
            //System.out.println(response.getData().getTensor().getValuesList());
            String file_name = "target/result"+img_name+".png";
            //image_pre = Image.fromarray(np.uint8(response))
            //image_pre.save(file_name)
            
            ByteArrayInputStream bais = new ByteArrayInputStream(response);  
            BufferedImage bi1 =ImageIO.read(bais);
            try {  
                File w2 = new File(file_name);//可以是jpg,png,gif格式  
                ImageIO.write(bi1, "png", w2);//不管输出什么格式图片，此处不需改动  
            } catch (IOException e) {  
                e.printStackTrace();  
            }finally{
               // bais.close();
            }
        }else{
            System.out.println("response is null");
        }
    }

    public static void client(WpaiDLPredictOnlineServiceGrpc.WpaiDLPredictOnlineServiceBlockingStub blockingStub){
        String imagePath = "target/test_data";
        if (CommonUtil.checkSystemIsWin()){
            imagePath = "demo\\model\\pytorch\\mnist\\test_dataee";
        }
        System.out.println(System.getProperty("user.dir"));
        PyTorchClient pyTorchClient = new PyTorchClient();
        File dataDir = new File(imagePath);
        for (File file : dataDir.listFiles()) {
            PredictionProtos.SeldonMessage request = pyTorchClient.getRequest(file);
            PredictionProtos.SeldonMessage response = blockingStub.withDeadlineAfter(10000000, TimeUnit.MILLISECONDS).pytorchPredict(request);
            pyTorchClient.saveResult(response,file.getName());
        }
    }
    
}
