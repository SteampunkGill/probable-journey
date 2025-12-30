package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDirConfig;

    @PostMapping("/upload/image")
    public ApiResponse<FileUploadDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false) String type) {
        
        System.out.println("[DEBUG] 收到图片上传请求: " + file.getOriginalFilename() + ", 类型: " + type);
        log.info("Uploading image: {}, type: {}", file.getOriginalFilename(), type);

        // 1. 严格的文件类型验证
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new com.milktea.backend.exception.ServiceException("INVALID_FILE_TYPE", "仅支持图片上传");
        }

        // 2. 严格的大小限制 (例如 2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new com.milktea.backend.exception.ServiceException("FILE_TOO_LARGE", "图片大小不能超过2MB");
        }

        try {
            // 3. 保存到本地
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
            
            java.io.File uploadDir = new java.io.File(uploadDirConfig);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            java.io.File dest = new java.io.File(uploadDir.getAbsolutePath() + java.io.File.separator + fileName);
            file.transferTo(dest);
            
            // 返回相对路径 URL，确保以 /uploads/ 开头
            String url = "/uploads/" + fileName;
            log.info("Image uploaded successfully, saved path: {}", url);
            
            FileUploadDTO response = FileUploadDTO.builder()
                    .url(url)
                    .thumbnailUrl(url) // 本地开发环境暂不处理缩略图
                    .size(file.getSize())
                    .width(800)
                    .height(600)
                    .build();
            
            return ApiResponse.success(response);
        } catch (java.io.IOException e) {
            log.error("文件上传失败", e);
            throw new com.milktea.backend.exception.ServiceException("UPLOAD_FAILED", "文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload/file")
    public ApiResponse<FileUploadDTO> uploadFile(
            @RequestParam("file") MultipartFile file) {
        
        log.info("Uploading file: {}", file.getOriginalFilename());
        
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.io.File uploadDir = new java.io.File(uploadDirConfig);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            java.io.File dest = new java.io.File(uploadDir.getAbsolutePath() + java.io.File.separator + fileName);
            file.transferTo(dest);

            FileUploadDTO response = FileUploadDTO.builder()
                    .url("/uploads/" + fileName)
                    .size(file.getSize())
                    .build();
            
            return ApiResponse.success(response);
        } catch (java.io.IOException e) {
            log.error("文件上传失败", e);
            throw new com.milktea.backend.exception.ServiceException("UPLOAD_FAILED", "文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/regions")
    public ApiResponse<List<RegionDTO>> getRegions() {
        log.info("Getting regions data");

        List<RegionDTO> regions = new ArrayList<>();

        List<RegionDTO> gdCities = new ArrayList<>();
        gdCities.add(RegionDTO.builder().code("440100").name("广州市").build());
        gdCities.add(RegionDTO.builder().code("440300").name("深圳市").build());
        
        regions.add(RegionDTO.builder()
                .code("440000")
                .name("广东省")
                .children(gdCities)
                .build());
        
        return ApiResponse.success(regions);
    }

    @GetMapping("/stores")
    public ApiResponse<List<StoreDTO>> getPublicStores() {
        log.info("Getting public stores list");

        List<StoreDTO> stores = new ArrayList<>();
        
        StoreDTO store1 = new StoreDTO();
        store1.setId(1L);
        store1.setName("奶茶店(总店)");
        store1.setAddress("广州市天河区XX路1号");
        store1.setPhone("020-12345678");
        store1.setStatus("OPEN");
        
        stores.add(store1);
        
        return ApiResponse.success(stores);
    }

    @GetMapping("/timestamp")
    public ApiResponse<Map<String, Long>> getTimestamp() {
        log.info("Getting server timestamp");
        return ApiResponse.success(Map.of("timestamp", System.currentTimeMillis()));
    }

    @GetMapping("/version")
    public ApiResponse<VersionDTO> checkVersion(
            @RequestParam(value = "platform", defaultValue = "android") String platform,
            @RequestParam(value = "version", required = false) String currentVersion) {
        log.info("Checking version for platform: {}, current version: {}", platform, currentVersion);
        
        VersionDTO version = VersionDTO.builder()
                .version("1.1.0")
                .updateContent("1. 优化下单流程\n2. 修复已知问题\n3. 提升系统稳定性")
                .downloadUrl("https://cdn.tea-milk.com/app/milktea-v1.1.0.apk")
                .forceUpdate(false)
                .build();
        
        return ApiResponse.success(version);
    }
}