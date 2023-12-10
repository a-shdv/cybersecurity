package com.company.cybersecurity.controllers;

import com.company.cybersecurity.exceptions.StorageFileNotFoundException;
import com.company.cybersecurity.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final StorageService storageService;

    @Autowired
    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        String test = (String) model.getAttribute("message");
        List<String> files = storageService.loadAll()
                .filter(path -> !path.getFileName().toString().equals(".DS_Store"))
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(HomeController.class, "serveFile", path.getFileName().toString())
                                .build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("files", files);
        if (test != null)
            model.addAttribute("message", test);
        return "home";
    }

    @PostMapping("/hash-message")
    public String hashMessage(@RequestParam("message") String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", storageService.hashMessage(message));
        return "redirect:/";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws NoSuchAlgorithmException, IOException {

        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
