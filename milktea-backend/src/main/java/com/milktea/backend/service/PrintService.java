package com.milktea.backend.service;

import com.milktea.backend.dto.PrintPrinterDTO;
import com.milktea.backend.dto.PrintTemplateDTO;
import com.milktea.backend.repository.PrintPrinterRepository;
import com.milktea.backend.repository.PrintTemplateRepository;
import com.milktea.backend.repository.StoreRepository;
import com.milktea.milktea_backend.model.entity.PrintPrinter;
import com.milktea.milktea_backend.model.entity.PrintTemplate;
import com.milktea.milktea_backend.model.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintService {

    private final PrintPrinterRepository printPrinterRepository;
    private final PrintTemplateRepository printTemplateRepository;
    private final StoreRepository storeRepository;

    // --- 打印设备管理 ---

    public List<PrintPrinterDTO> getAllPrinters() {
        return printPrinterRepository.findAll().stream()
                .map(this::convertToPrinterDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrintPrinterDTO savePrinter(PrintPrinterDTO dto) {
        PrintPrinter printer = dto.getId() != null ? 
                printPrinterRepository.findById(dto.getId()).orElse(new PrintPrinter()) : 
                new PrintPrinter();
        
        printer.setName(dto.getName());
        printer.setSn(dto.getSn());
        printer.setKey(dto.getKey());
        printer.setType(dto.getType());
        printer.setStatus(dto.getStatus());
        
        if (dto.getStoreId() != null) {
            Store store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Store not found"));
            printer.setStore(store);
        }
        
        return convertToPrinterDTO(printPrinterRepository.save(printer));
    }

    @Transactional
    public void deletePrinter(Long id) {
        printPrinterRepository.deleteById(id);
    }

    // --- 小票模板设置 ---

    public List<PrintTemplateDTO> getAllTemplates() {
        return printTemplateRepository.findAll().stream()
                .map(this::convertToTemplateDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrintTemplateDTO saveTemplate(PrintTemplateDTO dto) {
        PrintTemplate template = dto.getId() != null ? 
                printTemplateRepository.findById(dto.getId()).orElse(new PrintTemplate()) : 
                new PrintTemplate();
        
        template.setName(dto.getName());
        template.setContent(dto.getContent());
        template.setIsDefault(dto.getIsDefault());
        
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            // 如果设置为默认，取消其他模板的默认状态
            printTemplateRepository.findAll().forEach(t -> {
                if (!t.getId().equals(template.getId())) {
                    t.setIsDefault(false);
                    printTemplateRepository.save(t);
                }
            });
        }
        
        return convertToTemplateDTO(printTemplateRepository.save(template));
    }

    @Transactional
    public void deleteTemplate(Long id) {
        printTemplateRepository.deleteById(id);
    }

    // --- 辅助方法 ---

    private PrintPrinterDTO convertToPrinterDTO(PrintPrinter printer) {
        PrintPrinterDTO dto = new PrintPrinterDTO();
        dto.setId(printer.getId());
        dto.setName(printer.getName());
        dto.setSn(printer.getSn());
        dto.setKey(printer.getKey());
        dto.setType(printer.getType());
        dto.setStatus(printer.getStatus());
        if (printer.getStore() != null) {
            dto.setStoreId(printer.getStore().getId());
            dto.setStoreName(printer.getStore().getName());
        }
        return dto;
    }

    private PrintTemplateDTO convertToTemplateDTO(PrintTemplate template) {
        PrintTemplateDTO dto = new PrintTemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setContent(template.getContent());
        dto.setIsDefault(template.getIsDefault());
        return dto;
    }
}