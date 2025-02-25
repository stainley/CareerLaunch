package com.salapp.ticket.authserver.controller;

import com.salapp.ticket.authserver.model.User;
import com.salapp.ticket.authserver.service.TwoFactorService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@Validated
@Slf4j
public class TwoFactorController {
    private final TwoFactorService twoFactorService;

    @Autowired
    public TwoFactorController(TwoFactorService twoFactorService) {
        this.twoFactorService = twoFactorService;
    }

    @GetMapping("/2fa/setup-or-verify")
    public String setupOrVerify(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (!user.isTwoFactorEnabled()) {

                String qrCode = twoFactorService.generateQrCodeData(user.getEmail(), user.getTotpSecret());
                model.addAttribute("qrCode", qrCode);
                model.addAttribute("email", user.getEmail());
                return "2fa-setup";
            }
            return "2fa-verify";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Authentication required");
            return "redirect:/login";
        } catch (QrGenerationException e) {
            log.error("QR Generation failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to generate QR code. Please try again.");
            return "redirect:/2fa/setup-or-verify";
        }
    }

    @GetMapping("/2fa/verify")
    public String verify(Authentication authentication, @RequestParam String code) {
        User user = (User) authentication.getPrincipal();
        if (twoFactorService.verifyTotpCode(user.getEmail(), code)) {
            // Proceed to consent or issue token
            return "redirect:/oauth2/consent";
        }
        return "2fa-verify"; // Retry on failure
    }

    @PostMapping("/2fa/verity")
    public String processVerification(
            Authentication authentication,
            @RequestParam @NotBlank @Pattern(regexp = "\\d{6}", message = "Code must be 6 digits") String code,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = getAuthenticatedUser(authentication);

            if (twoFactorService.verifyTotpCode(user.getEmail(), code)) {
                log.info("2FA verification successful for {}", user.getEmail());
                return "redirect:/oauth2/consent";
            }

            log.warn("Invalid 2FA code attempt for {}", user.getEmail());
            redirectAttributes.addFlashAttribute("error", "Invalid verification code");

        } catch (IllegalStateException e) {
            log.warn("Unauthorized 2FA verification attempt");
            redirectAttributes.addFlashAttribute("error", "Authentication required");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("2FA verification error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Verification service error");
        }

        return "redirect:/2fa/verify";
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("Invalid authentication state");
        }
        return (User) authentication.getPrincipal();
    }
}
