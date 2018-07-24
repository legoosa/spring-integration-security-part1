package org.poc.file.app.resource;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.poc.file.app.service.FtpService;
import org.poc.file.app.vo.FileDownloadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file/")
public class FileApi {

	@Autowired
	private FtpService ftpService;
	
	@PostMapping(value = "/download")
    public ResponseEntity<byte[]> testDownload(@RequestBody FileDownloadVO vo) throws IOException {
        File file = ftpService.downloadFile(vo.getFileName(), "F:\\tmp\\" + vo.getFileName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", vo.getFileName());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }
    
	@PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadFile) {

    	System.out.println("Single file upload!");

        if (uploadFile.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }

        try {
            ftpService.uploadFile(uploadFile);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully uploaded - " +
                uploadFile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }
}
