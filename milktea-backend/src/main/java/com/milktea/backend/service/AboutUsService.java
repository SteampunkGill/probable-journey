package com.milktea.backend.service;

import com.milktea.backend.dto.AboutUsDTO;
import com.milktea.backend.repository.AboutUsRepository;
import com.milktea.milktea_backend.model.entity.AboutUs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AboutUsService {

    private final AboutUsRepository aboutUsRepository;

    public AboutUsDTO getAboutUs() {
        AboutUs aboutUs = aboutUsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    AboutUs defaultAbout = AboutUs.builder()
                            .title("关于我们")
                            .content("欢迎来到我们的奶茶店！")
                            .build();
                    return aboutUsRepository.save(defaultAbout);
                });
        return convertToDTO(aboutUs);
    }

    @Transactional
    public AboutUsDTO updateAboutUs(AboutUsDTO dto) {
        AboutUs aboutUs = aboutUsRepository.findFirstByOrderByIdAsc()
                .orElse(new AboutUs());
        
        aboutUs.setTitle(dto.getTitle());
        aboutUs.setContent(dto.getContent());
        aboutUs.setLogoUrl(dto.getLogoUrl());
        aboutUs.setContactPhone(dto.getContactPhone());
        aboutUs.setAddress(dto.getAddress());
        aboutUs.setLatitude(dto.getLatitude());
        aboutUs.setLongitude(dto.getLongitude());
        
        aboutUs = aboutUsRepository.save(aboutUs);
        return convertToDTO(aboutUs);
    }

    private AboutUsDTO convertToDTO(AboutUs aboutUs) {
        AboutUsDTO dto = new AboutUsDTO();
        dto.setTitle(aboutUs.getTitle());
        dto.setContent(aboutUs.getContent());
        dto.setLogoUrl(aboutUs.getLogoUrl());
        dto.setContactPhone(aboutUs.getContactPhone());
        dto.setAddress(aboutUs.getAddress());
        dto.setLatitude(aboutUs.getLatitude());
        dto.setLongitude(aboutUs.getLongitude());
        return dto;
    }
}