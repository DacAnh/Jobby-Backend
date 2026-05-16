package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.controller.error.StorageException;
import vn.hoidanit.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
//           Khi cần upload file thì phải ở dạng form-data thay vì json
//           Tức lấy @RequestParam thay vì @RequestBody
//           MultipartFile là class dùng để thao tác với file upload
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, IOException, StorageException {
//        Skipp validate
        if (file==null || file.isEmpty()) {
            throw new StorageException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "pdf", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("Invalid file format. ");
        }
//        Tạo folder nếu nó chưa tồn tại
        this.fileService.createDirectory(baseURI+folder);
//        Lưu trữ file
        String uploadFile = this.fileService.store(file,folder);

        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(resUploadFileDTO);
    }
}
