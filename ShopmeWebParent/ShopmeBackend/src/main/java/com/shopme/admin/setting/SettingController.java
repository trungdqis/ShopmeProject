package com.shopme.admin.setting;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {

    private final SettingService settingService;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public SettingController(SettingService settingService, CurrencyRepository currencyRepository) {
        this.settingService = settingService;
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> settings = settingService.listAlLSettings();
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("currencies", currencies);

        for (Setting setting : settings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile, HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag settingBag = settingService.getGeneralSettings();

        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);

        updateSettingValuesFromForm(request, settingBag.list());

        redirectAttributes.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            String uploadDir = "../site-logo/";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));

        Optional<Currency> currencyById = currencyRepository.findById(currencyId);

        if (currencyById.isPresent()) {
            Currency currency = currencyById.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settings) {
        for (Setting setting : settings) {
            String value = request.getParameter(setting.getKey());

            if (null != value) {
                setting.setValue(value);
            }
        }

        settingService.saveAll(settings);
    }
}
