package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.PrintPrinterDTO;
import com.milktea.backend.dto.PrintTemplateDTO;
import com.milktea.backend.service.PrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/print")
@RequiredArgsConstructor
public class AdminPrintController {

    private final PrintService printService;

    // --- 打印设备管理 ---

    @GetMapping("/printers")
    public ApiResponse<List<PrintPrinterDTO>> getPrinters() {
        return ApiResponse.success(printService.getAllPrinters());
    }

    @PostMapping("/printers")
    public ApiResponse<PrintPrinterDTO> createPrinter(@RequestBody PrintPrinterDTO printerDTO) {
        return ApiResponse.success(printService.savePrinter(printerDTO));
    }

    @PutMapping("/printers/{id}")
    public ApiResponse<PrintPrinterDTO> updatePrinter(@PathVariable Long id, @RequestBody PrintPrinterDTO printerDTO) {
        printerDTO.setId(id);
        return ApiResponse.success(printService.savePrinter(printerDTO));
    }

    @DeleteMapping("/printers/{id}")
    public ApiResponse<Void> deletePrinter(@PathVariable Long id) {
        printService.deletePrinter(id);
        return ApiResponse.success(null);
    }

    // --- 小票模板设置 ---

    @GetMapping("/templates")
    public ApiResponse<List<PrintTemplateDTO>> getTemplates() {
        return ApiResponse.success(printService.getAllTemplates());
    }

    @PostMapping("/templates")
    public ApiResponse<PrintTemplateDTO> createTemplate(@RequestBody PrintTemplateDTO templateDTO) {
        return ApiResponse.success(printService.saveTemplate(templateDTO));
    }

    @PutMapping("/templates/{id}")
    public ApiResponse<PrintTemplateDTO> updateTemplate(@PathVariable Long id, @RequestBody PrintTemplateDTO templateDTO) {
        templateDTO.setId(id);
        return ApiResponse.success(printService.saveTemplate(templateDTO));
    }

    @DeleteMapping("/templates/{id}")
    public ApiResponse<Void> deleteTemplate(@PathVariable Long id) {
        printService.deleteTemplate(id);
        return ApiResponse.success(null);
    }
}