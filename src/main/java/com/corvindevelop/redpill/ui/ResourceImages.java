package com.corvindevelop.redpill.ui;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

final class ResourceImages {
    private ResourceImages() {
    }

    static Image load(String resourcePath) {
        try (InputStream input = ResourceImages.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Missing image resource: " + resourcePath);
            }
            return ImageIO.read(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load image resource: " + resourcePath, e);
        }
    }
}
