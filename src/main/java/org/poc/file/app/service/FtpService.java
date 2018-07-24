package org.poc.file.app.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.poc.file.app.FtpConfig;
import org.poc.file.app.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FtpService {

	@Autowired
    private FtpRemoteFileTemplate remoteFileTemplate;

    @Autowired
    private FtpConfig.FtpGateway gateway;

    public File downloadFile(String fileName, String savePath) {
        return remoteFileTemplate.execute(session -> {
            boolean existFile = session.exists(fileName);
            if (existFile) {
                InputStream is = session.readRaw(fileName);
                return FileUtils.convertInputStreamToFile(is, savePath);
            } else {
            	System.out.println("file : {} not exist"+ fileName);
                return null;
            }
        });
    }

    public void uploadFile(MultipartFile multipartFile) throws IOException {
        gateway.send(FileUtils.convert(multipartFile));
    }
}
