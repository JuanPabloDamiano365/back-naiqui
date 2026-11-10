package com.uade.e_commerce.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.e_commerce.exception.BusinessException;

@Service
public class ProductoImagenStorageService {

    private static final long MAX_BYTES = 5 * 1024 * 1024;
    private static final long MAX_PIXELS = 16_000_000;

    private final Path uploadRoot;

    public ProductoImagenStorageService(@Value("${naiqui.upload.directory:./uploads}") String uploadDirectory) {
        this.uploadRoot = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    public String guardar(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_BYTES) {
            throw new BusinessException("La imagen debe tener contenido y pesar como máximo 5 MB.");
        }

        try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) throw new BusinessException("El archivo no es una imagen válida.");
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new BusinessException("Solo se admiten imágenes PNG o JPEG.");

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!format.equals("png") && !format.equals("jpeg") && !format.equals("jpg")) {
                    throw new BusinessException("Solo se admiten imágenes PNG o JPEG.");
                }
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS) {
                    throw new BusinessException("La resolución máxima permitida es de 16 megapíxeles.");
                }

                BufferedImage image = reader.read(0);
                if (image == null) throw new BusinessException("El archivo no es una imagen válida.");
                String extension = format.equals("png") ? ".png" : ".jpg";
                String filename = UUID.randomUUID() + extension;
                Path productImages = uploadRoot.resolve("products").normalize();
                Files.createDirectories(productImages);
                Path destination = productImages.resolve(filename).normalize();
                if (!destination.startsWith(productImages)) {
                    throw new BusinessException("No se pudo guardar la imagen.");
                }
                if (!ImageIO.write(image, format.equals("png") ? "png" : "jpeg", destination.toFile())) {
                    throw new BusinessException("No se pudo procesar la imagen.");
                }
                return "/uploads/products/" + filename;
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new BusinessException("No se pudo leer o guardar la imagen.");
        }
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }
}
