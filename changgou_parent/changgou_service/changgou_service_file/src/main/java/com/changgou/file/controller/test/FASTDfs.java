package com.changgou.file.controller.test;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/a")
public class FASTDfs {

    @PostMapping("/aa")
    public Result shangChuang(MultipartFile file) throws Exception {
        //1.加载配置文件
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        ClientGlobal.init(path);
        //2.创建TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //3.创建TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //4.创建StorageServer
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        //5.创建TrackerServer
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        byte[] bytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();//全路径名
        String extension = FilenameUtils.getExtension(originalFilename);//文件类型
        //6.上传文件 参数1文件字节数组  参数2文件扩展缀名 参数3文件原始属性
        String[] fileId = storageClient.upload_file(bytes, extension, null);//返回路径
        String visitPath = "http://192.168.200.128:8080/"+fileId[0]+"/"+fileId[1];//访问路径
        return new Result(true, StatusCode.OK,"文件上传成功",visitPath) ;
    }

}
